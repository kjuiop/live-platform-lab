import React, { useState, useRef, useEffect } from 'react';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';

const API_BASE = `${process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8090'}/api/v1`;

type EmbeddingStatus = 'none' | 'pending' | 'done';

interface Product {
  id: number;
  name: string;
  status: string;
  price: number;
  stockQuantity: number;
  sortOrder: number;
  description?: string;
  ingredients?: string;
  usageMethod?: string;
  manufacturer?: string;
  createdAt?: string;
  updatedAt?: string;
}

interface Document {
  id: string;
  name: string;
  size: string;
  uploadedAt: string;
  status: EmbeddingStatus;
  chunkCount?: number;
}

interface QnAItem {
  id: number;
  question: string;
  answer: string;
  isLoading?: boolean;
}

interface RelatedBroadcast {
  id: string;
  title: string;
  status: 'scheduled' | 'live' | 'ended';
  viewerCount?: number;
}


const MOCK_DOCUMENTS: Record<string, Document[]> = {
  P001: [
    { id: 'D001', name: '워터프루프_립스틱_성분표.pdf', size: '1.2 MB', uploadedAt: '2026-04-10', status: 'done', chunkCount: 24 },
    { id: 'D002', name: '립스틱_사용설명서.pdf', size: '0.8 MB', uploadedAt: '2026-04-10', status: 'done', chunkCount: 15 },
  ],
  P002: [
    { id: 'D003', name: '비타민C_세럼_성분분석.pdf', size: '2.1 MB', uploadedAt: '2026-04-08', status: 'done', chunkCount: 38 },
  ],
  P003: [],
};

const MOCK_BROADCASTS: Record<string, RelatedBroadcast[]> = {
  P001: [
    { id: 'B001', title: '봄맞이 뷰티 라이브', status: 'live', viewerCount: 1243 },
  ],
  P002: [
    { id: 'B002', title: '스킨케어 집중 케어', status: 'scheduled' },
  ],
  P003: [
    { id: 'B003', title: '파운데이션 비교 테스트', status: 'ended', viewerCount: 3892 },
  ],
};

const MOCK_AI_ANSWERS: Record<string, string> = {
  '성분': '이 제품의 주요 성분으로는 보습 효과가 뛰어난 글리세린과 히알루론산이 포함되어 있으며, 피부과 테스트를 완료한 저자극 포뮬러입니다.',
  '지속력': '워터프루프 포뮬러 적용으로 최대 12시간 지속됩니다. 물이나 땀에도 번짐 없이 유지됩니다.',
  '사용법': '세안 후 스킨케어 마지막 단계에 사용해주세요. 소량을 덜어 얼굴 전체에 고르게 펴 바르면 됩니다.',
  '가격': `정상가 기준이며, 방송 라이브 중 특가 혜택이 적용될 수 있습니다. 상세 가격은 방송을 참고해주세요.`,
  '부작용': '민감성 피부의 경우 사용 전 팔 안쪽에 패치 테스트를 권장합니다. 이상이 있을 경우 즉시 사용을 중단하고 전문가와 상담하세요.',
  default: '해당 상품의 등록된 정보를 기반으로 답변드립니다. 더 구체적인 질문을 입력해주시면 더 정확한 답변을 제공할 수 있습니다.',
};

const categoryColors: Record<string, string> = {
  '뷰티': '#f472b6',
  '스킨케어': '#60a5fa',
  '패션': '#34d399',
  '식품': '#fbbf24',
};

const broadcastStatusLabel = { scheduled: '예정', live: '라이브 중', ended: '종료' };
const broadcastStatusClass = { scheduled: 'bs-scheduled', live: 'bs-live', ended: 'bs-ended' };

export default function ProductDetail() {
  const router = useRouter();
  const { id } = router.query;

  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const documents = MOCK_DOCUMENTS[id as string] ?? [];
  const relatedBroadcasts = MOCK_BROADCASTS[id as string] ?? [];

  const [qnaList, setQnaList] = useState<QnAItem[]>([]);
  const [aiInput, setAiInput] = useState('');
  const [isDragOver, setIsDragOver] = useState(false);
  const [mockDocs, setMockDocs] = useState<Document[]>(documents);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const qnaBottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    fetch(`${API_BASE}/products/${id}`)
      .then((res) => {
        if (!res.ok) { router.push('/products'); return null; }
        return res.json();
      })
      .then((json) => {
        if (json) setProduct(json.data);
        setLoading(false);
      });
  }, [id]);

  useEffect(() => {
    qnaBottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [qnaList]);

  // 문서 목록은 id가 바뀔 때 동기화
  useEffect(() => {
    setMockDocs(MOCK_DOCUMENTS[id as string] ?? []);
  }, [id]);

  const askAI = () => {
    const q = aiInput.trim();
    if (!q) return;
    setAiInput('');
    const newItem: QnAItem = { id: Date.now(), question: q, answer: '', isLoading: true };
    setQnaList((prev) => [...prev, newItem]);
    const keyword = Object.keys(MOCK_AI_ANSWERS).find((k) => k !== 'default' && q.includes(k));
    const answer = MOCK_AI_ANSWERS[keyword ?? 'default'];
    setTimeout(() => {
      setQnaList((prev) => prev.map((item) => item.id === newItem.id ? { ...item, answer, isLoading: false } : item));
    }, 1200);
  };

  const handleFileDrop = (files: FileList | null) => {
    if (!files || files.length === 0) return;
    Array.from(files).forEach((file) => {
      const newDoc: Document = {
        id: `D${Date.now()}`,
        name: file.name,
        size: `${(file.size / 1024 / 1024).toFixed(1)} MB`,
        uploadedAt: new Date().toISOString().slice(0, 10),
        status: 'pending',
      };
      setMockDocs((prev) => [...prev, newDoc]);
      // 임베딩 완료 시뮬레이션
      setTimeout(() => {
        setMockDocs((prev) =>
          prev.map((d) => d.id === newDoc.id ? { ...d, status: 'done', chunkCount: Math.floor(Math.random() * 30) + 10 } : d)
        );
      }, 2000);
    });
  };

  if (!id || loading) return null;

  if (!product) {
    return (
      <>
        <Head><title>상품을 찾을 수 없음</title></Head>
        <style jsx global>{`* { margin:0;padding:0;box-sizing:border-box; } body { font-family:-apple-system,sans-serif; background:#0f172a; color:#e2e8f0; min-height:100vh; } a { text-decoration:none;color:inherit; }`}</style>
        <div style={{ maxWidth: 600, margin: '0 auto', padding: '80px 24px', textAlign: 'center' }}>
          <div style={{ fontSize: 48, marginBottom: 20 }}>📦</div>
          <h1 style={{ fontSize: 24, fontWeight: 700, color: '#f1f5f9', marginBottom: 12 }}>상품을 찾을 수 없습니다</h1>
          <p style={{ color: '#64748b', marginBottom: 32 }}>요청하신 상품이 존재하지 않거나 삭제되었습니다.</p>
          <Link href="/products" style={{ padding: '10px 24px', background: 'linear-gradient(135deg,#6366f1,#8b5cf6)', color: 'white', borderRadius: 8, fontSize: 14, fontWeight: 600 }}>
            목록으로 돌아가기
          </Link>
        </div>
      </>
    );
  }

  const totalChunks = mockDocs.filter((d) => d.status === 'done').reduce((sum, d) => sum + (d.chunkCount ?? 0), 0);

  return (
    <>
      <Head><title>{product.name} — Live Platform Lab</title></Head>
      <style jsx global>{`* { margin:0;padding:0;box-sizing:border-box; } body { font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif; background:#0f172a; color:#e2e8f0; min-height:100vh; } a { text-decoration:none;color:inherit; }`}</style>
      <style jsx>{`
        .container { max-width: 1100px; margin: 0 auto; padding: 40px 24px; }
        .nav { display: flex; align-items: center; gap: 8px; margin-bottom: 40px; font-size: 13px; color: #64748b; }
        .nav a:hover { color: #a5b4fc; }
        .nav-sep { color: #334155; }

        .layout { display: grid; grid-template-columns: 1fr 360px; gap: 24px; align-items: start; }

        /* 좌측 */
        .left { display: flex; flex-direction: column; gap: 20px; }

        .info-card { background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08); border-radius: 16px; padding: 28px; }
        .info-top { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; margin-bottom: 8px; }
        .info-name { font-size: 26px; font-weight: 800; color: #f1f5f9; }
        .embed-badge { font-size: 11px; font-weight: 700; padding: 4px 12px; border-radius: 999px; white-space: nowrap; flex-shrink: 0; margin-top: 4px; }
        .embed-done { background: rgba(16,185,129,0.12); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.25); }
        .embed-pending { background: rgba(251,191,36,0.12); color: #fcd34d; border: 1px solid rgba(251,191,36,0.25); }
        .embed-none { background: rgba(100,116,139,0.12); color: #94a3b8; border: 1px solid rgba(100,116,139,0.25); }
        .info-meta { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
        .cat-badge { font-size: 12px; font-weight: 700; padding: 3px 12px; border-radius: 999px; }
        .info-price { font-size: 22px; font-weight: 800; color: #f1f5f9; }
        .info-desc { font-size: 14px; color: #94a3b8; line-height: 1.75; margin-bottom: 24px; }
        .info-details { display: flex; flex-direction: column; gap: 14px; border-top: 1px solid rgba(255,255,255,0.07); padding-top: 20px; }
        .detail-row { display: flex; gap: 12px; }
        .detail-label { font-size: 12px; font-weight: 700; color: #64748b; width: 56px; flex-shrink: 0; padding-top: 1px; }
        .detail-val { font-size: 13px; color: #94a3b8; line-height: 1.65; }

        /* 문서 */
        .section-card { background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08); border-radius: 16px; padding: 24px; }
        .section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
        .section-title { font-size: 15px; font-weight: 700; color: #f1f5f9; }
        .section-sub { font-size: 11px; color: #64748b; margin-top: 2px; }
        .chunk-badge { font-size: 11px; font-weight: 700; padding: 3px 10px; border-radius: 999px; background: rgba(16,185,129,0.1); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.2); }

        .drop-zone { border: 1.5px dashed rgba(99,102,241,0.3); border-radius: 10px; padding: 20px; text-align: center; cursor: pointer; transition: border-color 0.2s, background 0.2s; margin-bottom: 14px; }
        .drop-zone:hover, .drop-zone.over { border-color: rgba(99,102,241,0.6); background: rgba(99,102,241,0.05); }
        .drop-zone-text { font-size: 13px; color: #475569; }
        .drop-zone-sub { font-size: 11px; color: #334155; margin-top: 4px; }

        .doc-list { display: flex; flex-direction: column; gap: 8px; }
        .doc-item { display: flex; align-items: center; gap: 12px; padding: 10px 14px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 8px; }
        .doc-icon { font-size: 18px; flex-shrink: 0; }
        .doc-info { flex: 1; min-width: 0; }
        .doc-name { font-size: 13px; font-weight: 600; color: #e2e8f0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
        .doc-meta { font-size: 11px; color: #475569; margin-top: 2px; }
        .doc-status { flex-shrink: 0; }
        .doc-badge { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 999px; }
        .doc-done { background: rgba(16,185,129,0.12); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.2); }
        .doc-pending { background: rgba(251,191,36,0.12); color: #fcd34d; border: 1px solid rgba(251,191,36,0.2); }

        .no-docs { font-size: 13px; color: #475569; text-align: center; padding: 16px 0; }

        /* 우측 */
        .right { display: flex; flex-direction: column; gap: 20px; position: sticky; top: 24px; }

        /* AI Q&A */
        .ai-card { background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08); border-radius: 16px; display: flex; flex-direction: column; max-height: 480px; overflow: hidden; }
        .ai-header { padding: 18px 20px; border-bottom: 1px solid rgba(255,255,255,0.07); }
        .ai-title { font-size: 15px; font-weight: 700; color: #f1f5f9; }
        .ai-sub { font-size: 11px; color: #64748b; margin-top: 3px; }
        .ai-body { flex: 1; overflow-y: auto; padding: 16px; display: flex; flex-direction: column; gap: 14px; scrollbar-width: thin; scrollbar-color: rgba(255,255,255,0.1) transparent; }
        .ai-body::-webkit-scrollbar { width: 4px; }
        .ai-body::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 2px; }
        .ai-empty { color: #475569; font-size: 13px; text-align: center; padding: 24px 0; line-height: 1.7; }
        .qna-item { display: flex; flex-direction: column; gap: 8px; padding: 12px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; }
        .qna-row { display: flex; align-items: flex-start; gap: 8px; }
        .qna-badge { font-size: 10px; font-weight: 700; padding: 2px 7px; border-radius: 999px; flex-shrink: 0; margin-top: 1px; }
        .qna-badge.q { background: rgba(99,102,241,0.2); color: #a5b4fc; border: 1px solid rgba(99,102,241,0.3); }
        .qna-badge.a { background: rgba(16,185,129,0.15); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.25); }
        .qna-text { font-size: 13px; color: #cbd5e1; line-height: 1.6; }
        .qna-text.answer { color: #94a3b8; }
        .qna-loading { font-size: 13px; color: #6ee7b7; }
        .dots::after { content: '...'; animation: dotanim 1.2s steps(4, end) infinite; }
        @keyframes dotanim { 0%,100% { content: ''; } 25% { content: '.'; } 50% { content: '..'; } 75% { content: '...'; } }
        .ai-input-row { padding: 12px 16px; border-top: 1px solid rgba(255,255,255,0.07); display: flex; gap: 8px; }
        .ai-input { flex: 1; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.08); border-radius: 8px; padding: 9px 12px; font-size: 13px; color: #f1f5f9; outline: none; }
        .ai-input:focus { border-color: #6366f1; }
        .ai-send { padding: 9px 14px; background: linear-gradient(135deg, #6366f1, #8b5cf6); color: white; border: none; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; white-space: nowrap; }
        .ai-send:disabled { opacity: 0.4; cursor: not-allowed; }

        /* 연결된 방송 */
        .broadcast-list { display: flex; flex-direction: column; gap: 8px; }
        .b-item { display: flex; align-items: center; gap: 10px; padding: 10px 14px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; transition: border-color 0.2s; }
        .b-item:hover { border-color: rgba(99,102,241,0.3); }
        .b-title { flex: 1; font-size: 13px; font-weight: 600; color: #e2e8f0; }
        .bs-live { font-size: 11px; font-weight: 700; padding: 2px 8px; border-radius: 999px; background: rgba(239,68,68,0.15); color: #fca5a5; border: 1px solid rgba(239,68,68,0.3); }
        .bs-scheduled { font-size: 11px; font-weight: 700; padding: 2px 8px; border-radius: 999px; background: rgba(251,191,36,0.12); color: #fcd34d; border: 1px solid rgba(251,191,36,0.25); }
        .bs-ended { font-size: 11px; font-weight: 700; padding: 2px 8px; border-radius: 999px; background: rgba(100,116,139,0.12); color: #94a3b8; border: 1px solid rgba(100,116,139,0.2); }
        .b-arrow { font-size: 12px; color: #334155; }
        .no-broadcast { font-size: 13px; color: #475569; text-align: center; padding: 12px 0; }

        @media (max-width: 800px) {
          .layout { grid-template-columns: 1fr; }
          .right { position: static; }
        }
      `}</style>

      <div className="container">
        <nav className="nav">
          <Link href="/">홈</Link>
          <span className="nav-sep">/</span>
          <Link href="/products">상품 관리</Link>
          <span className="nav-sep">/</span>
          <span style={{ color: '#94a3b8' }}>{product.name}</span>
        </nav>

        <div className="layout">
          {/* 좌측 */}
          <div className="left">
            {/* 상품 정보 */}
            <div className="info-card">
              <div className="info-top">
                <h1 className="info-name">{product.name}</h1>
                <span className="embed-badge embed-none">{product.status}</span>
              </div>
              <div className="info-meta">
                <span className="info-price">{Number(product.price).toLocaleString()}원</span>
                <span style={{ fontSize: 13, color: '#64748b' }}>재고 {product.stockQuantity}개</span>
              </div>
              {product.description && <p className="info-desc">{product.description}</p>}
              <div className="info-details">
                {product.ingredients && (
                  <div className="detail-row">
                    <span className="detail-label">주요 성분</span>
                    <span className="detail-val">{product.ingredients}</span>
                  </div>
                )}
                {product.usageMethod && (
                  <div className="detail-row">
                    <span className="detail-label">사용법</span>
                    <span className="detail-val">{product.usageMethod}</span>
                  </div>
                )}
                {product.manufacturer && (
                  <div className="detail-row">
                    <span className="detail-label">제조사</span>
                    <span className="detail-val">{product.manufacturer}</span>
                  </div>
                )}
              </div>
            </div>

            {/* 등록 문서 */}
            <div className="section-card">
              <div className="section-header">
                <div>
                  <div className="section-title">등록된 문서</div>
                  <div className="section-sub">업로드한 PDF가 AI Q&A의 근거 자료로 활용됩니다</div>
                </div>
                {totalChunks > 0 && (
                  <span className="chunk-badge">{totalChunks} chunks</span>
                )}
              </div>

              {/* 드래그 업로드 */}
              <div
                className={`drop-zone ${isDragOver ? 'over' : ''}`}
                onClick={() => fileInputRef.current?.click()}
                onDragOver={(e) => { e.preventDefault(); setIsDragOver(true); }}
                onDragLeave={() => setIsDragOver(false)}
                onDrop={(e) => { e.preventDefault(); setIsDragOver(false); handleFileDrop(e.dataTransfer.files); }}
              >
                <div className="drop-zone-text">PDF 파일을 드래그하거나 클릭하여 업로드</div>
                <div className="drop-zone-sub">업로드 즉시 AI 임베딩이 시작됩니다</div>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept=".pdf"
                  multiple
                  style={{ display: 'none' }}
                  onChange={(e) => handleFileDrop(e.target.files)}
                />
              </div>

              {mockDocs.length === 0 ? (
                <div className="no-docs">등록된 문서가 없습니다.</div>
              ) : (
                <div className="doc-list">
                  {mockDocs.map((doc) => (
                    <div key={doc.id} className="doc-item">
                      <span className="doc-icon">📄</span>
                      <div className="doc-info">
                        <div className="doc-name">{doc.name}</div>
                        <div className="doc-meta">
                          {doc.size} · {doc.uploadedAt}
                          {doc.chunkCount && ` · ${doc.chunkCount} chunks`}
                        </div>
                      </div>
                      <div className="doc-status">
                        <span className={`doc-badge ${doc.status === 'done' ? 'doc-done' : 'doc-pending'}`}>
                          {doc.status === 'done' ? '완료' : '처리 중...'}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* 우측 */}
          <div className="right">
            {/* AI Q&A 테스트 */}
            <div className="ai-card">
              <div className="ai-header">
                <div className="ai-title">AI Q&A 테스트</div>
                <div className="ai-sub">등록된 문서를 바탕으로 AI가 답변합니다</div>
              </div>
              <div className="ai-body">
                {qnaList.length === 0 ? (
                  <div className="ai-empty">
                    이 상품에 대해<br />궁금한 점을 물어보세요
                  </div>
                ) : (
                  qnaList.map((item) => (
                    <div key={item.id} className="qna-item">
                      <div className="qna-row">
                        <span className="qna-badge q">Q</span>
                        <span className="qna-text">{item.question}</span>
                      </div>
                      <div className="qna-row">
                        <span className="qna-badge a">AI</span>
                        {item.isLoading ? (
                          <span className="qna-loading">답변 생성 중<span className="dots" /></span>
                        ) : (
                          <span className="qna-text answer">{item.answer}</span>
                        )}
                      </div>
                    </div>
                  ))
                )}
                <div ref={qnaBottomRef} />
              </div>
              <div className="ai-input-row">
                <input
                  className="ai-input"
                  value={aiInput}
                  onChange={(e) => setAiInput(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && askAI()}
                  placeholder="예) 성분이 어떻게 되나요?"
                />
                <button className="ai-send" onClick={askAI} disabled={!aiInput.trim()}>질문</button>
              </div>
            </div>

            {/* 연결된 방송 */}
            <div className="section-card">
              <div className="section-header">
                <div>
                  <div className="section-title">연결된 방송</div>
                  <div className="section-sub">이 상품이 사용된 방송 목록</div>
                </div>
              </div>
              {relatedBroadcasts.length === 0 ? (
                <div className="no-broadcast">연결된 방송이 없습니다.</div>
              ) : (
                <div className="broadcast-list">
                  {relatedBroadcasts.map((b) => (
                    <Link key={b.id} href={`/broadcasts/${b.id}`} className="b-item">
                      <span className="b-title">{b.title}</span>
                      <span className={broadcastStatusClass[b.status]}>
                        {b.status === 'live' && '● '}{broadcastStatusLabel[b.status]}
                      </span>
                      <span className="b-arrow">›</span>
                    </Link>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
