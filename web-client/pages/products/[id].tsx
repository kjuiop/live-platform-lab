import React, { useState, useRef, useEffect } from 'react';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';

const API_BASE = `${process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8090'}/api/v1`;

interface Product {
  id: number;
  name: string;
  status: string;
  embeddingStatus: 'NONE' | 'PENDING' | 'WORKING' | 'DONE';
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

interface FaqSampleItem {
  question: string;
  answer: string;
}

interface LinkedCampaign {
  campaignId: number;
  title: string;
  status: 'SCHEDULED' | 'ON_AIR' | 'ENDED';
  scheduledAt: string | null;
  startedAt: string | null;
  endedAt: string | null;
}

const broadcastStatusLabel: Record<string, string> = { SCHEDULED: '예정', ON_AIR: '라이브 중', ENDED: '종료' };
const broadcastStatusClass: Record<string, string> = { SCHEDULED: 'bs-scheduled', ON_AIR: 'bs-live', ENDED: 'bs-ended' };

export default function ProductDetail() {
  const router = useRouter();
  const { id } = router.query;

  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [documents, setDocuments] = useState<ApiDocument[]>([]);
  const [campaigns, setCampaigns] = useState<LinkedCampaign[]>([]);
  const [embeddingIds, setEmbeddingIds] = useState<Set<number>>(new Set());

  const [productEmbedding, setProductEmbedding] = useState(false);
  const [bulkEmbedding, setBulkEmbedding] = useState(false);

  const [faqSamples, setFaqSamples] = useState<FaqSampleItem[]>([]);
  const [faqLoading, setFaqLoading] = useState(false);
  const [faqGenerated, setFaqGenerated] = useState(false);

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
    if (!id) return;
    fetch(`${API_BASE}/products/${id}/campaigns`)
      .then((res) => res.ok ? res.json() : null)
      .then((json) => {
        if (json?.data?.campaigns) setCampaigns(json.data.campaigns);
      })
      .catch(() => {});
  }, [id]);

  useEffect(() => {
    if (!id) return;
    fetch(`${API_BASE}/products/${id}/faq-samples`)
      .then((res) => res.ok ? res.json() : null)
      .then((json) => {
        if (json?.data?.items?.length > 0) {
          setFaqSamples(json.data.items);
          setFaqGenerated(true);
        }
      })
      .catch(() => {});
  }, [id]);

  useEffect(() => {
    qnaBottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [qnaList]);

  const handleEmbedProductInfo = async () => {
    setProductEmbedding(true);
    try {
      const res = await fetch(`${API_BASE}/products/${id}/embed-info`, { method: 'POST' });
      if (res.ok) {
        const json = await res.json();
        setProduct((prev) => prev ? { ...prev, embeddingStatus: json.data.embeddingStatus } : prev);
      }
    } catch {
      // ignore
    } finally {
      setProductEmbedding(false);
    }
  };

  const handleEmbedAll = async () => {
    setBulkEmbedding(true);
    try {
      await fetch(`${API_BASE}/products/${id}/documents/embed-all`, { method: 'POST' });
      const docsRes = await fetch(`${API_BASE}/products/${id}/documents`);
      const docsJson = await docsRes.json();
      if (docsJson?.data?.items) setDocuments(docsJson.data.items);
    } catch {
      // ignore
    } finally {
      setBulkEmbedding(false);
    }
  };

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

  const handleGenerateFaqSamples = async () => {
    setFaqLoading(true);
    try {
      await fetch(`${API_BASE}/products/${id}/ai/faq-samples`, { method: 'POST' });
      const res = await fetch(`${API_BASE}/products/${id}/faq-samples`);
      const json = await res.json();
      if (res.ok && json?.data?.items) {
        setFaqSamples(json.data.items);
        setFaqGenerated(true);
      }
    } catch {
      // ignore
    } finally {
      setFaqLoading(false);
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
        .bulk-embed-btn { font-size: 11px; font-weight: 700; padding: 4px 12px; border-radius: 8px; background: rgba(99,102,241,0.15); color: #a5b4fc; border: 1px solid rgba(99,102,241,0.3); cursor: pointer; transition: background 0.15s; }
        .bulk-embed-btn:hover { background: rgba(99,102,241,0.3); }
        .bulk-embed-btn:disabled { opacity: 0.5; cursor: not-allowed; }
        .info-embed-btn { font-size: 11px; font-weight: 700; padding: 4px 12px; border-radius: 8px; background: rgba(16,185,129,0.12); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.25); cursor: pointer; transition: background 0.15s; white-space: nowrap; }
        .info-embed-btn:hover { background: rgba(16,185,129,0.25); }
        .info-embed-btn:disabled { opacity: 0.5; cursor: not-allowed; }
        .embed-status-none { background: rgba(100,116,139,0.12); color: #94a3b8; border: 1px solid rgba(100,116,139,0.25); }
        .embed-status-done { background: rgba(16,185,129,0.12); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.25); }

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

        /* 사전 Q&A */
        .faq-list { display: flex; flex-direction: column; gap: 12px; max-height: 440px; overflow-y: auto; scrollbar-width: thin; scrollbar-color: rgba(255,255,255,0.1) transparent; }
        .faq-list::-webkit-scrollbar { width: 4px; }
        .faq-list::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 2px; }
        .faq-item { display: flex; flex-direction: column; gap: 8px; padding: 12px 14px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; }
        .faq-row { display: flex; align-items: flex-start; gap: 8px; }
        .faq-badge { font-size: 10px; font-weight: 700; padding: 2px 7px; border-radius: 999px; flex-shrink: 0; margin-top: 1px; }
        .faq-badge.q { background: rgba(99,102,241,0.2); color: #a5b4fc; border: 1px solid rgba(99,102,241,0.3); }
        .faq-badge.a { background: rgba(16,185,129,0.15); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.25); }
        .faq-text { font-size: 13px; line-height: 1.65; }
        .faq-text.q { color: #cbd5e1; font-weight: 600; }
        .faq-text.a { color: #94a3b8; }

        /* 연결된 방송 */
        .broadcast-list { display: flex; flex-direction: column; gap: 8px; max-height: 320px; overflow-y: auto; scrollbar-width: thin; scrollbar-color: rgba(255,255,255,0.1) transparent; }
        .broadcast-list::-webkit-scrollbar { width: 4px; }
        .broadcast-list::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 2px; }
        .b-item { display: flex; align-items: center; gap: 12px; padding: 10px 14px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 8px; transition: border-color 0.2s; cursor: pointer; }
        .b-item:hover { border-color: rgba(99,102,241,0.4); background: rgba(99,102,241,0.05); }
        .b-info { flex: 1; min-width: 0; }
        .b-name { font-size: 13px; font-weight: 600; color: #e2e8f0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
        .b-meta { font-size: 11px; color: #475569; margin-top: 2px; }
        .b-status { flex-shrink: 0; }
        .bs-live { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 999px; background: rgba(239,68,68,0.15); color: #fca5a5; border: 1px solid rgba(239,68,68,0.3); }
        .bs-scheduled { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 999px; background: rgba(251,191,36,0.12); color: #fcd34d; border: 1px solid rgba(251,191,36,0.25); }
        .bs-ended { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 999px; background: rgba(100,116,139,0.12); color: #94a3b8; border: 1px solid rgba(100,116,139,0.2); }
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
                <div style={{ flexShrink: 0 }}>
                  {product.embeddingStatus === 'DONE' ? (
                    <span className="embed-badge embed-status-done">정보 임베딩 완료</span>
                  ) : productEmbedding ? (
                    <span className="embed-badge embed-status-none">임베딩 중...</span>
                  ) : (
                    <button className="info-embed-btn" onClick={handleEmbedProductInfo}>
                      정보 임베딩
                    </button>
                  )}
                </div>
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
                {documents.some((d) => d.embedYn === 'N') && (
                  <button className="bulk-embed-btn" onClick={handleEmbedAll} disabled={bulkEmbedding}>
                    {bulkEmbedding ? '처리 중...' : '전체 임베딩'}
                  </button>
                )}
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

            {/* 방송 전 사전 Q&A */}
            <div className="section-card">
              <div className="section-header">
                <div>
                  <div className="section-title">방송 전 사전 Q&A</div>
                  <div className="section-sub">임베딩된 문서 기반으로 예상 질문과 답변을 생성합니다</div>
                </div>
                <button
                  className="bulk-embed-btn"
                  onClick={handleGenerateFaqSamples}
                  disabled={faqLoading}
                >
                  {faqLoading ? '생성 중...' : faqGenerated ? '재생성' : 'Q&A 생성'}
                </button>
              </div>

              {faqSamples.length === 0 ? (
                <div className="no-docs">
                  {faqGenerated ? '생성된 Q&A가 없습니다.' : 'Q&A 생성 버튼을 눌러 예상 질문을 준비하세요.'}
                </div>
              ) : (
                <div className="faq-list">
                  {faqSamples.map((item, idx) => (
                    <div key={idx} className="faq-item">
                      <div className="faq-row">
                        <span className="faq-badge q">Q</span>
                        <span className="faq-text q">{item.question}</span>
                      </div>
                      <div className="faq-row">
                        <span className="faq-badge a">A</span>
                        <span className="faq-text a">{item.answer}</span>
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
              {campaigns.length === 0 ? (
                <div className="no-broadcast">연결된 방송이 없습니다.</div>
              ) : (
                <div className="broadcast-list">
                  {campaigns.map((c) => (
                    <div
                      key={c.campaignId}
                      className="b-item"
                      onClick={() => router.push(`/broadcasts/${c.campaignId}`)}
                    >
                      <div className="b-info">
                        <div className="b-name">{c.title}</div>
                        <div className="b-meta">
                          {c.status === 'SCHEDULED' && c.scheduledAt && `예정일: ${new Date(c.scheduledAt).toLocaleDateString('ko-KR')}`}
                          {c.status === 'ON_AIR' && c.startedAt && `시작: ${new Date(c.startedAt).toLocaleDateString('ko-KR')}`}
                          {c.status === 'ENDED' && c.endedAt && `종료: ${new Date(c.endedAt).toLocaleDateString('ko-KR')}`}
                        </div>
                      </div>
                      <div className="b-status">
                        <span className={broadcastStatusClass[c.status]}>
                          {c.status === 'ON_AIR' && '● '}{broadcastStatusLabel[c.status]}
                        </span>
                      </div>
                    </div>
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
