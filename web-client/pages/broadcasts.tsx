import React, { useState } from 'react';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';

type BroadcastStatus = 'scheduled' | 'live' | 'ended';

interface Broadcast {
  id: string;
  title: string;
  productName: string;
  productId: string;
  status: BroadcastStatus;
  startedAt?: string;
}

interface Product {
  id: string;
  name: string;
  category: string;
}

const MOCK_PRODUCTS: Product[] = [
  { id: 'P001', name: '워터프루프 립스틱', category: '뷰티' },
  { id: 'P002', name: '비타민C 세럼', category: '스킨케어' },
  { id: 'P003', name: '쿠션 파운데이션', category: '뷰티' },
];

const MOCK_BROADCASTS: Broadcast[] = [
  { id: 'B001', title: '봄맞이 뷰티 라이브', productName: '워터프루프 립스틱', productId: 'P001', status: 'live', startedAt: '14:00' },
  { id: 'B002', title: '스킨케어 집중 케어', productName: '비타민C 세럼', productId: 'P002', status: 'scheduled' },
  { id: 'B003', title: '파운데이션 비교 테스트', productName: '쿠션 파운데이션', productId: 'P003', status: 'ended', startedAt: '10:00' },
];

const statusLabel: Record<BroadcastStatus, string> = {
  scheduled: '예정',
  live: '라이브 중',
  ended: '종료',
};

const statusClass: Record<BroadcastStatus, string> = {
  scheduled: 'status-scheduled',
  live: 'status-live',
  ended: 'status-ended',
};

export default function Broadcasts() {
  const router = useRouter();
  const [broadcasts] = useState<Broadcast[]>(MOCK_BROADCASTS);

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
        .card-title { font-size: 18px; font-weight: 700; color: #f1f5f9; margin-bottom: 8px; }
        .card-meta { display: flex; align-items: center; gap: 10px; font-size: 13px; color: #64748b; }
        .product-tag { background: rgba(99,102,241,0.12); border: 1px solid rgba(99,102,241,0.2); color: #a5b4fc; padding: 2px 10px; border-radius: 999px; font-size: 12px; font-weight: 600; }
        .started-at { font-size: 12px; }
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
            <Link href="/products" className="btn-products">
              상품 관리
            </Link>
            <button className="btn-add" onClick={() => router.push('/broadcasts/new')}>+ 방송 생성</button>
          </div>
        </div>
        <div className="rag-notice">
          방송에 연결된 상품 정보는 AI Q&A에 자동으로 활용됩니다.
          <Link href="/products" className="rag-notice-link">상품 관리 →</Link>
        </div>

        <div className="list">
          {broadcasts.map((b) => (
            <div key={b.id} className={`card ${b.status === 'live' ? 'live' : ''}`}>
              <div className="card-left">
                <div className="card-title">{b.title}</div>
                <div className="card-meta">
                  <span className="product-tag">📦 {b.productName}</span>
                  {b.startedAt && <span className="started-at">시작 {b.startedAt}</span>}
                </div>
              </div>
              <div className="card-right">
                <span className={`status-badge ${statusClass[b.status]}`}>
                  {statusLabel[b.status]}
                </span>
                <button
                  className={`btn-enter ${b.status === 'live' ? 'btn-enter-live' : 'btn-enter-normal'}`}
                  onClick={() => router.push(`/broadcasts/${b.id}`)}
                >
                  {b.status === 'ended' ? '다시보기' : '입장'}
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

    </>
  );
}
