package org.giglab.live.commerce.core.product.application.usecase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import org.giglab.live.commerce.core.product.application.dto.pdf.ParsedProductResult;
import org.giglab.live.commerce.core.product.domain.exception.ProductDomainException;
import org.giglab.live.commerce.core.product.domain.exception.ProductErrorCode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service
public class ParseProductPdfUseCase {

  private static final String SYSTEM_PROMPT =
      """
      당신은 상품 정보 추출 전문가입니다.
      아래 텍스트에서 상품 정보를 추출해 반드시 다음 JSON 형식으로만 응답하세요.
      없는 필드는 null로 표시하세요.
      {
        "name": "상품명",
        "price": 가격숫자(원, 숫자만),
        "description": "상품 설명",
        "manufacturer": "제조사",
        "ingredients": "성분",
        "usageMethod": "사용 방법",
        "extractedText": "원본 추출 텍스트 전체"
      }
      """;

  private final ChatClient chatClient;
  private final String uploadBasePath;

  public ParseProductPdfUseCase(
      ChatModel chatModel, @Value("${app.upload.base-path:uploads}") String uploadBasePath) {
    this.chatClient = ChatClient.create(chatModel);
    this.uploadBasePath = uploadBasePath;
  }

  public ParsedProductResult execute(String filename, byte[] fileBytes) {
    try {
      saveFile(filename, fileBytes);
    } catch (IOException e) {
      throw new ProductDomainException(
          ProductErrorCode.PDF_READ_FAILED, "PDF 저장 실패: " + e.getMessage());
    }

    String extractedText = extractText(fileBytes);
    return parseWithLlm(extractedText);
  }

  private void saveFile(String filename, byte[] fileBytes) throws IOException {
    Path dir = Path.of(uploadBasePath, "products/pdf");
    Files.createDirectories(dir);
    Path filePath = dir.resolve(filename);
    Files.copy(
        new ByteArrayResource(fileBytes).getInputStream(),
        filePath,
        StandardCopyOption.REPLACE_EXISTING);
  }

  private String extractText(byte[] fileBytes) {
    PagePdfDocumentReader reader =
        new PagePdfDocumentReader(
            new ByteArrayResource(fileBytes),
            PdfDocumentReaderConfig.builder().withPagesPerDocument(1).build());

    return reader.get().stream()
        .limit(10)
        .map(Document::getFormattedContent)
        .collect(Collectors.joining("\n"));
  }

  private ParsedProductResult parseWithLlm(String text) {
    return chatClient
        .prompt()
        .system(SYSTEM_PROMPT)
        .user("다음 텍스트에서 상품 정보를 추출해주세요:\n\n" + text)
        .call()
        .entity(ParsedProductResult.class);
  }
}
