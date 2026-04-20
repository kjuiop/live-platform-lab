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
}

export default function Products() {
  const router = useRouter();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [nextCursor, setNextCursor] = useState<number | null>(null);
  const [hasNext, setHasNext] = useState(false);

  const fetchProducts = async (cursor?: number) => {
    const params = new URLSearchParams({ size: '20' });
    if (cursor != null) params.set('cursor', String(cursor));
    const res = await fetch(`${API_BASE}/products?${params}`);
    const json = await res.json();
    const { items, nextCursor: nc, hasNext: hn } = json.data;
    setProducts((prev) => cursor != null ? [...prev, ...items] : items);
    setNextCursor(nc ?? null);
    setHasNext(hn);
    setLoading(false);
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
        .price { font-size: 18px; font-weight: 800; color: #f1f5f9; }
        .badge-embed { font-size: 11px; font-weight: 700; padding: 4px 11px; border-radius: 999px; }
        .embed-done { background: rgba(16,185,129,0.12); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.25); }
        .embed-pending { background: rgba(251,191,36,0.12); color: #fcd34d; border: 1px solid rgba(251,191,36,0.25); }
        .embed-none { background: rgba(100,116,139,0.12); color: #94a3b8; border: 1px solid rgba(100,116,139,0.25); }
        .loading { text-align: center; color: #475569; padding: 60px 0; font-size: 14px; }
        .btn-more { display: block; margin: 28px auto 0; padding: 10px 28px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #94a3b8; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
        .btn-more:hover { background: rgba(255,255,255,0.1); }
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
                    <span className="embed-none" style={{ fontSize: '11px', fontWeight: 700, padding: '4px 11px', borderRadius: '999px' }}>
                      재고 {p.stockQuantity}개
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
