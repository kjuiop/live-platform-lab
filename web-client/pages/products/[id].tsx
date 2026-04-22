import React, { useState, useRef, useEffect } from 'react';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';

const API_BASE = `${process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8090'}/api/v1`;

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

interface ApiDocument {
  documentId: number;
  filename: string;
  embedYn: 'Y' | 'N';
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


const broadcastStatusLabel = { scheduled: '예정', live: '라이브 중', ended: '종료' };
const broadcastStatusClass = { scheduled: 'bs-scheduled', live: 'bs-live', ended: 'bs-ended' };

export default function ProductDetail() {
  const router = useRouter();
  const { id } = router.query;

  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [documents, setDocuments] = useState<ApiDocument[]>([]);
  const [embeddingIds, setEmbeddingIds] = useState<Set<number>>(new Set());
  const relatedBroadcasts = MOCK_BROADCASTS[id as string] ?? [];

  const [qnaList, setQnaList] = useState<QnAItem[]>([]);
  const [aiInput, setAiInput] = useState('');
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
      })
      .catch(() => {
        setLoading(false);
        router.push('/products');
      });
  }, [id]);

  useEffect(() => {
    if (!id) return;
    fetch(`${API_BASE}/products/${id}/documents`)
      .then((res) => res.ok ? res.json() : null)
      .then((json) => {
        if (json?.data?.items) setDocuments(json.data.items);
      })
      .catch(() => {});
  }, [id]);

  useEffect(() => {
    qnaBottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [qnaList]);

  const handleEmbed = async (documentId: number) => {
    setEmbeddingIds((prev) => new Set(prev).add(documentId));
    try {
      const res = await fetch(`${API_BASE}/products/documents/${documentId}/embed`, {
        method: 'POST',
      });
      if (res.ok) {
        setDocuments((prev) =>
          prev.map((doc) =>
            doc.documentId === documentId ? { ...doc, embedYn: 'Y' } : doc
          )
        );
      }
    } catch {
      // ignore
    } finally {
      setEmbeddingIds((prev) => {
        const next = new Set(prev);
        next.delete(documentId);
        return next;
      });
    }
  };

  const askAI = async () => {
    const q = aiInput.trim();
    if (!q) return;
    setAiInput('');
    const newItem: QnAItem = { id: Date.now(), question: q, answer: '', isLoading: true };
    setQnaList((prev) => [...prev, newItem]);

    try {
      const res = await fetch(`${API_BASE}/products/${id}/ai/ask`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ question: q }),
      });
      const json = await res.json();
      const answer = res.ok ? json.data.answer : '답변을 가져오지 못했습니다.';
      setQnaList((prev) =>
        prev.map((item) => item.id === newItem.id ? { ...item, answer, isLoading: false } : item)
      );
    } catch {
      setQnaList((prev) =>
        prev.map((item) =>
          item.id === newItem.id ? { ...item, answer: '오류가 발생했습니다.', isLoading: false } : item
        )
      );
    }
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

        .doc-list { display: flex; flex-direction: column; gap: 8px; }
        .doc-item { display: flex; align-items: center; gap: 12px; padding: 10px 14px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 8px; }
        .doc-icon { font-size: 18px; flex-shrink: 0; }
        .doc-info { flex: 1; min-width: 0; }
        .doc-name { font-size: 13px; font-weight: 600; color: #e2e8f0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
        .doc-meta { font-size: 11px; color: #475569; margin-top: 2px; }
        .doc-status { flex-shrink: 0; }
        .doc-badge { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 999px; }
        .doc-done { background: rgba(16,185,129,0.12); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.2); }
        .doc-embed-btn { font-size: 10px; font-weight: 700; padding: 3px 10px; border-radius: 999px; background: rgba(99,102,241,0.15); color: #a5b4fc; border: 1px solid rgba(99,102,241,0.3); cursor: pointer; transition: background 0.15s; }
        .doc-embed-btn:hover { background: rgba(99,102,241,0.3); }
        .doc-embed-btn:disabled { opacity: 0.5; cursor: not-allowed; }
        .doc-embedding { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 999px; background: rgba(251,191,36,0.12); color: #fcd34d; border: 1px solid rgba(251,191,36,0.2); }

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
              </div>

              {documents.length === 0 ? (
                <div className="no-docs">등록된 문서가 없습니다.</div>
              ) : (
                <div className="doc-list">
                  {documents.map((doc) => {
                    const isEmbedding = embeddingIds.has(doc.documentId);
                    return (
                      <div key={doc.documentId} className="doc-item">
                        <span className="doc-icon">📄</span>
                        <div className="doc-info">
                          <div className="doc-name">{doc.filename}</div>
                          <div className="doc-meta">ID: {doc.documentId}</div>
                        </div>
                        <div className="doc-status">
                          {doc.embedYn === 'Y' ? (
                            <span className="doc-badge doc-done">임베딩 완료</span>
                          ) : isEmbedding ? (
                            <span className="doc-embedding">처리 중...</span>
                          ) : (
                            <button
                              className="doc-embed-btn"
                              onClick={() => handleEmbed(doc.documentId)}
                            >
                              임베딩 시작
                            </button>
                          )}
                        </div>
                      </div>
                    );
                  })}
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
