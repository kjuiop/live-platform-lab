import React, { useState, useEffect } from 'react';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';

const API_BASE = `${process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8090'}/api/v1`;
const PAGE_SIZE = 20;

interface Campaign {
  id: number;
  title: string;
  description: string;
  status: string;
  scheduledAt: string;
  productCount: number;
}

const statusLabel: Record<string, string> = {
  SCHEDULED: '예정',
  ON_AIR: '라이브 중',
  ENDED: '종료',
};

const statusClass: Record<string, string> = {
  SCHEDULED: 'status-scheduled',
  ON_AIR: 'status-live',
  ENDED: 'status-ended',
};

export default function Broadcasts() {
  const router = useRouter();
  const [campaigns, setCampaigns] = useState<Campaign[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalCount, setTotalCount] = useState(0);

  const fetchCampaigns = async (p: number) => {
    setLoading(true);
    try {
      const params = new URLSearchParams({ page: String(p), size: String(PAGE_SIZE) });
      const res = await fetch(`${API_BASE}/campaigns/page?${params}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const json = await res.json();
      const { items, totalPages: tp, totalCount: tc } = json.data;
      setCampaigns(items);
      setTotalPages(tp);
      setTotalCount(tc);
    } catch (err) {
      setError('방송 목록을 불러오는 데 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchCampaigns(page); }, [page]);

  const formatDate = (iso: string) => {
    if (!iso) return '';
    const d = new Date(iso);
    return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
  };

  return (
    <>
      <Head><title>방송 목록 — Live Platform Lab</title></Head>
      <style jsx global>{`* { margin: 0; padding: 0; box-sizing: border-box; } body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0f172a; color: #e2e8f0; min-height: 100vh; } a { text-decoration: none; color: inherit; }`}</style>
      <style jsx>{`
        .container { max-width: 1100px; margin: 0 auto; padding: 40px 24px; }
        .nav { display: flex; align-items: center; gap: 8px; margin-bottom: 40px; font-size: 14px; color: #64748b; }
        .nav a:hover { color: #a5b4fc; }
        .nav-sep { color: #334155; }
        .header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
        .title { font-size: 28px; font-weight: 700; color: #f1f5f9; }
        .header-actions { display: flex; align-items: center; gap: 10px; }
        .btn-products { padding: 10px 18px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #94a3b8; border-radius: 8px; font-size: 14px; font-weight: 600; transition: border-color 0.2s, color 0.2s; }
        .btn-products:hover { border-color: rgba(99,102,241,0.4); color: #a5b4fc; }
        .btn-add { padding: 10px 22px; background: linear-gradient(135deg, #6366f1, #8b5cf6); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: opacity 0.2s, transform 0.2s; }
        .btn-add:hover { opacity: 0.9; transform: translateY(-1px); }
        .rag-notice { display: flex; align-items: center; padding: 10px 16px; background: rgba(99,102,241,0.06); border: 1px solid rgba(99,102,241,0.15); border-radius: 10px; font-size: 13px; color: #64748b; margin-bottom: 28px; }
        .rag-notice-link { margin-left: auto; font-size: 13px; font-weight: 600; color: #6366f1; }
        .rag-notice-link:hover { color: #a5b4fc; }
        .list { display: flex; flex-direction: column; gap: 14px; }
        .card { background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08); border-radius: 14px; padding: 24px; display: flex; align-items: center; justify-content: space-between; gap: 20px; transition: border-color 0.2s; }
        .card:hover { border-color: rgba(99,102,241,0.3); }
        .card.live { border-color: rgba(239,68,68,0.4); background: rgba(239,68,68,0.04); }
        .card-left { flex: 1; min-width: 0; }
        .card-title { font-size: 18px; font-weight: 700; color: #f1f5f9; margin-bottom: 8px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .card-meta { display: flex; align-items: center; gap: 10px; font-size: 13px; color: #64748b; }
        .product-tag { background: rgba(99,102,241,0.12); border: 1px solid rgba(99,102,241,0.2); color: #a5b4fc; padding: 2px 10px; border-radius: 999px; font-size: 12px; font-weight: 600; }
        .scheduled-at { font-size: 12px; }
        .card-right { display: flex; align-items: center; gap: 14px; flex-shrink: 0; }
        .status-badge { font-size: 12px; font-weight: 700; padding: 4px 12px; border-radius: 999px; }
        .status-live { background: rgba(239,68,68,0.15); color: #fca5a5; border: 1px solid rgba(239,68,68,0.3); }
        .status-live::before { content: '● '; }
        .status-scheduled { background: rgba(251,191,36,0.12); color: #fcd34d; border: 1px solid rgba(251,191,36,0.25); }
        .status-ended { background: rgba(100,116,139,0.12); color: #94a3b8; border: 1px solid rgba(100,116,139,0.2); }
        .btn-enter { padding: 8px 20px; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; border: none; transition: opacity 0.2s; }
        .btn-enter-live { background: #ef4444; color: white; }
        .btn-enter-live:hover { opacity: 0.85; }
        .btn-enter-normal { background: rgba(99,102,241,0.15); border: 1px solid rgba(99,102,241,0.3); color: #a5b4fc; }
        .btn-enter-normal:hover { background: rgba(99,102,241,0.25); }
        .loading { text-align: center; padding: 60px; color: #475569; font-size: 14px; }
        .error-msg { text-align: center; padding: 60px; color: #f87171; font-size: 14px; }
        .empty { text-align: center; padding: 60px; color: #475569; font-size: 14px; }
        .pagination { display: flex; align-items: center; justify-content: center; gap: 8px; margin-top: 32px; }
        .page-info { font-size: 13px; color: #64748b; margin: 0 8px; }
        .btn-page { padding: 8px 16px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #94a3b8; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
        .btn-page:hover:not(:disabled) { background: rgba(255,255,255,0.1); color: #e2e8f0; }
        .btn-page:disabled { opacity: 0.35; cursor: not-allowed; }
        .total-count { font-size: 13px; color: #475569; margin-bottom: 12px; }
      `}</style>

      <div className="container">
        <nav className="nav">
          <Link href="/">홈</Link>
          <span className="nav-sep">/</span>
          <span>방송 목록</span>
        </nav>

        <div className="header">
          <h1 className="title">방송 목록</h1>
          <div className="header-actions">
            <Link href="/products" className="btn-products">상품 관리</Link>
            <button className="btn-add" onClick={() => router.push('/broadcasts/new')}>+ 방송 생성</button>
          </div>
        </div>
        <div className="rag-notice">
          방송에 연결된 상품 정보는 AI Q&A에 자동으로 활용됩니다.
          <Link href="/products" className="rag-notice-link">상품 관리 →</Link>
        </div>

        {loading ? (
          <div className="loading">불러오는 중...</div>
        ) : error ? (
          <div className="error-msg">{error}</div>
        ) : campaigns.length === 0 ? (
          <div className="empty">등록된 방송이 없습니다.</div>
        ) : (
          <>
            <div className="total-count">전체 {totalCount.toLocaleString()}개</div>
            <div className="list">
              {campaigns.map((c) => (
                <div key={c.id} className={`card ${c.status === 'ON_AIR' ? 'live' : ''}`}>
                  <div className="card-left">
                    <div className="card-title">{c.title}</div>
                    <div className="card-meta">
                      <span className="product-tag">📦 상품 {c.productCount}개</span>
                      {c.scheduledAt && <span className="scheduled-at">예정 {formatDate(c.scheduledAt)}</span>}
                    </div>
                  </div>
                  <div className="card-right">
                    <span className={`status-badge ${statusClass[c.status] ?? 'status-ended'}`}>
                      {statusLabel[c.status] ?? c.status}
                    </span>
                    <button
                      className={`btn-enter ${c.status === 'ON_AIR' ? 'btn-enter-live' : 'btn-enter-normal'}`}
                      onClick={() => router.push(`/broadcasts/${c.id}`)}
                    >
                      {c.status === 'ENDED' ? '다시보기' : '입장'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
            {totalPages > 1 && (
              <div className="pagination">
                <button className="btn-page" onClick={() => setPage(1)} disabled={page === 1}>처음</button>
                <button className="btn-page" onClick={() => setPage((p) => p - 1)} disabled={page === 1}>이전</button>
                <span className="page-info">{page} / {totalPages}</span>
                <button className="btn-page" onClick={() => setPage((p) => p + 1)} disabled={page === totalPages}>다음</button>
                <button className="btn-page" onClick={() => setPage(totalPages)} disabled={page === totalPages}>마지막</button>
              </div>
            )}
          </>
        )}
      </div>
    </>
  );
}
