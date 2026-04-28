import React, { useState, useEffect } from 'react';
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
  categoryName?: string;
  embeddingStatus: 'NONE' | 'PENDING' | 'WORKING' | 'DONE';
}

export default function Products() {
  const router = useRouter();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [nextCursor, setNextCursor] = useState<number | null>(null);
  const [hasNext, setHasNext] = useState(false);

  const fetchProducts = async (cursor?: number) => {
    try {
      const params = new URLSearchParams({ size: '20' });
      if (cursor != null) params.set('cursor', String(cursor));
      const res = await fetch(`${API_BASE}/products?${params}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const json = await res.json();
      const { items, nextCursor: nc, hasNext: hn } = json.data;
      setProducts((prev) => cursor != null ? [...prev, ...items] : items);
      setNextCursor(nc ?? null);
      setHasNext(hn);
    } catch {
      setError('상품을 불러오는 데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchProducts(); }, []);

  return (
    <>
      <Head><title>상품 관리 — Live Platform Lab</title></Head>
      <style jsx global>{`* { margin: 0; padding: 0; box-sizing: border-box; } body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0f172a; color: #e2e8f0; min-height: 100vh; } a { text-decoration: none; color: inherit; }`}</style>
      <style jsx>{`
        .container { max-width: 1100px; margin: 0 auto; padding: 40px 24px; }
        .nav { display: flex; align-items: center; gap: 8px; margin-bottom: 40px; font-size: 14px; color: #64748b; }
        .nav a:hover { color: #a5b4fc; }
        .nav-sep { color: #334155; }
        .header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 32px; }
        .title { font-size: 28px; font-weight: 700; color: #f1f5f9; }
        .btn-add { padding: 10px 22px; background: linear-gradient(135deg, #6366f1, #8b5cf6); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: opacity 0.2s, transform 0.2s; }
        .btn-add:hover { opacity: 0.9; transform: translateY(-1px); }
        .grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
        .card { background: #1e293b; border: 1px solid rgba(255,255,255,0.1); border-radius: 16px; padding: 24px; transition: border-color 0.18s, box-shadow 0.18s; cursor: pointer; }
        .card:hover { border-color: rgba(99,102,241,0.4); box-shadow: 0 4px 24px rgba(0,0,0,0.3); }
        .card-top { display: flex; align-items: flex-start; justify-content: space-between; gap: 10px; margin-bottom: 10px; }
        .card-name { font-size: 17px; font-weight: 700; color: #f1f5f9; line-height: 1.3; }
        .badge-category { font-size: 11px; font-weight: 700; padding: 3px 10px; border-radius: 999px; white-space: nowrap; flex-shrink: 0; background: rgba(99,102,241,0.15); color: #a5b4fc; border: 1px solid rgba(99,102,241,0.25); }
        .card-desc { font-size: 13px; color: #64748b; line-height: 1.65; margin-bottom: 20px; }
        .card-divider { height: 1px; background: rgba(255,255,255,0.07); margin-bottom: 16px; }
        .card-bottom { display: flex; align-items: center; justify-content: space-between; }
        .card-ai { margin-top: 14px; border-radius: 10px; padding: 9px 14px; display: flex; align-items: center; gap: 8px; font-size: 12px; font-weight: 600; }
        .card-ai-dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }
        .card-ai-label { flex: 1; }
        .card-ai-icon { font-size: 14px; }
        .ai-done { background: linear-gradient(90deg, rgba(16,185,129,0.1), rgba(16,185,129,0.04)); border: 1px solid rgba(16,185,129,0.2); color: #34d399; }
        .ai-done .card-ai-dot { background: #10b981; box-shadow: 0 0 6px rgba(16,185,129,0.7); }
        .ai-working { background: linear-gradient(90deg, rgba(251,191,36,0.1), rgba(251,191,36,0.04)); border: 1px solid rgba(251,191,36,0.2); color: #fbbf24; animation: pulse-border 1.8s ease-in-out infinite; }
        .ai-working .card-ai-dot { background: #f59e0b; box-shadow: 0 0 6px rgba(251,191,36,0.7); animation: pulse-dot 1.8s ease-in-out infinite; }
        .ai-none { background: rgba(100,116,139,0.06); border: 1px solid rgba(100,116,139,0.14); color: #475569; }
        .ai-none .card-ai-dot { background: #334155; }
        @keyframes pulse-dot { 0%,100% { opacity: 1; } 50% { opacity: 0.3; } }
        @keyframes pulse-border { 0%,100% { border-color: rgba(251,191,36,0.2); } 50% { border-color: rgba(251,191,36,0.45); } }
        .price { font-size: 18px; font-weight: 800; color: #f1f5f9; }
        .badge-embed { font-size: 11px; font-weight: 700; padding: 4px 11px; border-radius: 999px; }
        .embed-none { background: rgba(100,116,139,0.12); color: #94a3b8; border: 1px solid rgba(100,116,139,0.25); }
        .loading { text-align: center; color: #475569; padding: 60px 0; font-size: 14px; }
        .btn-more { display: block; margin: 28px auto 0; padding: 10px 28px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #94a3b8; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
        .btn-more:hover { background: rgba(255,255,255,0.1); }
        .error-msg { text-align: center; color: #f87171; padding: 60px 0; font-size: 14px; }
        @media (max-width: 768px) { .grid { grid-template-columns: 1fr; } }
      `}</style>

      <div className="container">
        <nav className="nav">
          <Link href="/">홈</Link>
          <span className="nav-sep">/</span>
          <span>상품 관리</span>
        </nav>

        <div className="header">
          <h1 className="title">상품 관리</h1>
          <button className="btn-add" onClick={() => router.push('/products/new')}>+ 상품 등록</button>
        </div>

        {loading ? (
          <div className="loading">불러오는 중...</div>
        ) : error ? (
          <div className="error-msg">{error}</div>
        ) : (
          <>
            <div className="grid">
              {products.map((p) => (
                <div key={p.id} className="card" onClick={() => router.push(`/products/${p.id}`)}>
                  <div className="card-top">
                    <span className="card-name">{p.name}</span>
                    {p.categoryName && <span className="badge-category">{p.categoryName}</span>}
                  </div>
                  <div className="card-divider" />
                  <div className="card-bottom">
                    <span className="price">{Number(p.price).toLocaleString()}원</span>
                    <span className="badge-embed embed-none">재고 {p.stockQuantity}개</span>
                  </div>
                  <div className={`card-ai ${
                    p.embeddingStatus === 'DONE' ? 'ai-done'
                    : p.embeddingStatus === 'WORKING' || p.embeddingStatus === 'PENDING' ? 'ai-working'
                    : 'ai-none'
                  }`}>
                    <span className="card-ai-dot" />
                    <span className="card-ai-label">
                      {p.embeddingStatus === 'DONE' ? 'AI 임베딩 완료'
                        : p.embeddingStatus === 'WORKING' ? 'AI 임베딩 중'
                        : p.embeddingStatus === 'PENDING' ? 'AI 임베딩 대기'
                        : 'AI 미임베딩'}
                    </span>
                    <span className="card-ai-icon">
                      {p.embeddingStatus === 'DONE' ? '✦' : p.embeddingStatus === 'NONE' ? '○' : '◌'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
            {hasNext && (
              <button className="btn-more" onClick={() => fetchProducts(nextCursor!)}>
                더 보기
              </button>
            )}
          </>
        )}
      </div>

    </>
  );
}
