import React, { useState } from 'react';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';

interface Product {
  id: string;
  name: string;
  category: string;
  price: number;
  description: string;
  embeddingStatus: 'none' | 'pending' | 'done';
}

const MOCK_PRODUCTS: Product[] = [
  { id: 'P001', name: '워터프루프 립스틱', category: '뷰티', price: 25000, description: '24시간 지속되는 방수 립스틱. 선명한 발색과 촉촉한 보습력을 동시에.', embeddingStatus: 'done' },
  { id: 'P002', name: '비타민C 세럼', category: '스킨케어', price: 48000, description: '고농도 비타민C 15% 함유. 미백과 탄력 개선에 효과적입니다.', embeddingStatus: 'done' },
  { id: 'P003', name: '쿠션 파운데이션', category: '뷰티', price: 35000, description: 'SPF50+ PA++++ 자외선 차단. 촉촉한 피부 표현에 최적화된 쿠션.', embeddingStatus: 'none' },
];

export default function BroadcastNew() {
  const router = useRouter();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [scheduledAt, setScheduledAt] = useState('');
  const [selectedProductId, setSelectedProductId] = useState('');
  const [errors, setErrors] = useState<{ title?: string; product?: string }>({});
  const [isSaving, setIsSaving] = useState(false);

  const validate = () => {
    const errs: { title?: string; product?: string } = {};
    if (!title.trim()) errs.title = '방송 제목을 입력해주세요.';
    if (!selectedProductId) errs.product = '상품을 선택해주세요.';
    return errs;
  };

  const handleSave = async () => {
    const errs = validate();
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }
    setIsSaving(true);
    // TODO: POST /api/v1/broadcasts
    await new Promise((r) => setTimeout(r, 700));
    router.push('/broadcasts');
  };

  const selectedProduct = MOCK_PRODUCTS.find((p) => p.id === selectedProductId);

  return (
    <>
      <Head><title>방송 등록 — Live Platform Lab</title></Head>
      <style jsx global>{`* { margin:0;padding:0;box-sizing:border-box; } body { font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif; background:#0f172a; color:#e2e8f0; min-height:100vh; } a { text-decoration:none;color:inherit; }`}</style>
      <style jsx>{`
        .container { max-width: 900px; margin: 0 auto; padding: 40px 24px; }
        .nav { display: flex; align-items: center; gap: 8px; margin-bottom: 40px; font-size: 13px; color: #64748b; }
        .nav a:hover { color: #a5b4fc; }
        .nav-sep { color: #334155; }

        .page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 32px; }
        .page-title { font-size: 26px; font-weight: 800; color: #f1f5f9; }
        .header-actions { display: flex; gap: 10px; }
        .btn-cancel { padding: 10px 22px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #94a3b8; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: background 0.2s; display: inline-flex; align-items: center; }
        .btn-cancel:hover { background: rgba(255,255,255,0.1); }
        .btn-save { padding: 10px 28px; background: linear-gradient(135deg, #6366f1, #8b5cf6); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 700; cursor: pointer; transition: opacity 0.2s; }
        .btn-save:hover:not(:disabled) { opacity: 0.88; }
        .btn-save:disabled { opacity: 0.5; cursor: not-allowed; }

        .layout { display: flex; flex-direction: column; gap: 20px; }

        .card { background: #1e293b; border: 1px solid rgba(255,255,255,0.1); border-radius: 16px; padding: 28px; }
        .card-title { font-size: 15px; font-weight: 700; color: #f1f5f9; margin-bottom: 20px; }

        .field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
        .field:last-child { margin-bottom: 0; }
        .field-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
        .label { font-size: 12px; font-weight: 700; color: #64748b; letter-spacing: 0.04em; text-transform: uppercase; }
        .input { width: 100%; padding: 11px 14px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; font-size: 14px; color: #f1f5f9; outline: none; transition: border-color 0.2s; font-family: inherit; }
        .input:focus { border-color: #6366f1; background: rgba(99,102,241,0.05); }
        .input.has-error { border-color: rgba(239,68,68,0.5); }
        .input::placeholder { color: #334155; }
        textarea.input { resize: vertical; min-height: 72px; line-height: 1.6; }
        .field-error { font-size: 12px; color: #f87171; }

        /* 상품 선택 */
        .product-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
        .product-card { background: rgba(255,255,255,0.03); border: 1.5px solid rgba(255,255,255,0.08); border-radius: 12px; padding: 16px; cursor: pointer; transition: border-color 0.15s, background 0.15s; }
        .product-card:hover { border-color: rgba(99,102,241,0.35); background: rgba(99,102,241,0.04); }
        .product-card.selected { border-color: #6366f1; background: rgba(99,102,241,0.08); }
        .product-card-top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 6px; }
        .product-name { font-size: 14px; font-weight: 700; color: #f1f5f9; }
        .product-cat { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 999px; background: rgba(99,102,241,0.15); color: #a5b4fc; border: 1px solid rgba(99,102,241,0.25); }
        .product-desc { font-size: 12px; color: #64748b; line-height: 1.55; margin-bottom: 10px; }
        .product-bottom { display: flex; align-items: center; justify-content: space-between; }
        .product-price { font-size: 13px; font-weight: 700; color: #e2e8f0; }
        .embed-badge { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 999px; }
        .embed-done { background: rgba(16,185,129,0.12); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.25); }
        .embed-none { background: rgba(100,116,139,0.12); color: #94a3b8; border: 1px solid rgba(100,116,139,0.25); }
        .select-check { width: 18px; height: 18px; border-radius: 50%; border: 2px solid #6366f1; background: #6366f1; display: flex; align-items: center; justify-content: center; font-size: 10px; color: white; flex-shrink: 0; margin-top: 2px; }
        .error-msg { font-size: 12px; color: #f87171; margin-top: 8px; }

        /* 선택된 상품 미리보기 */
        .selected-preview { margin-top: 16px; padding: 14px 16px; background: rgba(99,102,241,0.06); border: 1px solid rgba(99,102,241,0.2); border-radius: 10px; display: flex; align-items: center; gap: 10px; }
        .selected-preview-label { font-size: 11px; font-weight: 700; color: #6366f1; flex-shrink: 0; }
        .selected-preview-name { font-size: 14px; font-weight: 600; color: #f1f5f9; }
        .selected-preview-link { margin-left: auto; font-size: 12px; color: #6366f1; font-weight: 600; flex-shrink: 0; }
        .selected-preview-link:hover { color: #a5b4fc; }

        @media (max-width: 640px) {
          .product-grid { grid-template-columns: 1fr; }
          .field-row { grid-template-columns: 1fr; }
        }
      `}</style>

      <div className="container">
        <nav className="nav">
          <Link href="/">홈</Link>
          <span className="nav-sep">/</span>
          <Link href="/broadcasts">방송 목록</Link>
          <span className="nav-sep">/</span>
          <span style={{ color: '#94a3b8' }}>방송 등록</span>
        </nav>

        <div className="page-header">
          <h1 className="page-title">방송 등록</h1>
          <div className="header-actions">
            <Link href="/broadcasts" className="btn-cancel">취소</Link>
            <button className="btn-save" onClick={handleSave} disabled={isSaving}>
              {isSaving ? '저장 중...' : '저장'}
            </button>
          </div>
        </div>

        <div className="layout">
          {/* 방송 정보 */}
          <div className="card">
            <div className="card-title">방송 정보</div>
            <div className="field">
              <label className="label">방송 제목 *</label>
              <input
                className={`input ${errors.title ? 'has-error' : ''}`}
                placeholder="예) 봄맞이 뷰티 라이브"
                value={title}
                onChange={(e) => { setTitle(e.target.value); setErrors((p) => ({ ...p, title: undefined })); }}
              />
              {errors.title && <span className="field-error">{errors.title}</span>}
            </div>
            <div className="field-row">
              <div className="field">
                <label className="label">방송 예정 시간</label>
                <input
                  className="input"
                  type="datetime-local"
                  value={scheduledAt}
                  onChange={(e) => setScheduledAt(e.target.value)}
                  style={{ colorScheme: 'dark' }}
                />
              </div>
            </div>
            <div className="field">
              <label className="label">방송 설명</label>
              <textarea
                className="input"
                placeholder="방송 내용을 간략하게 소개해주세요."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                rows={3}
              />
            </div>
          </div>

          {/* 상품 선택 */}
          <div className="card">
            <div className="card-title">상품 선택 *</div>
            <div className="product-grid">
              {MOCK_PRODUCTS.map((p) => (
                <div
                  key={p.id}
                  className={`product-card ${selectedProductId === p.id ? 'selected' : ''}`}
                  onClick={() => { setSelectedProductId(p.id); setErrors((prev) => ({ ...prev, product: undefined })); }}
                >
                  <div className="product-card-top">
                    <span className="product-name">{p.name}</span>
                    {selectedProductId === p.id && <span className="select-check">✓</span>}
                  </div>
                  <p className="product-desc">{p.description}</p>
                  <div className="product-bottom">
                    <span className="product-price">{p.price.toLocaleString()}원</span>
                    <span className={`embed-badge ${p.embeddingStatus === 'done' ? 'embed-done' : 'embed-none'}`}>
                      {p.embeddingStatus === 'done' ? '임베딩 완료' : 'AI 미등록'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
            {errors.product && <div className="error-msg">{errors.product}</div>}

            {selectedProduct && (
              <div className="selected-preview">
                <span className="selected-preview-label">선택됨</span>
                <span className="selected-preview-name">{selectedProduct.name}</span>
                <Link href={`/products/${selectedProduct.id}`} className="selected-preview-link">
                  상품 상세 →
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
