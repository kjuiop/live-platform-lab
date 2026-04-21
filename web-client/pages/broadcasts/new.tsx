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
  categoryName?: string;
}

export default function BroadcastNew() {
  const router = useRouter();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [scheduledAt, setScheduledAt] = useState('');
  const [selectedProductIds, setSelectedProductIds] = useState<number[]>([]);
  const [showProductPicker, setShowProductPicker] = useState(false);
  const [errors, setErrors] = useState<{ title?: string; product?: string; submit?: string }>({});
  const [isSaving, setIsSaving] = useState(false);

  const [products, setProducts] = useState<Product[]>([]);
  const [productsLoading, setProductsLoading] = useState(false);
  const [productKeyword, setProductKeyword] = useState('');

  const fetchProducts = (keyword: string) => {
    setProductsLoading(true);
    const params = new URLSearchParams({ size: '20' });
    if (keyword.trim()) params.set('keyword', keyword.trim());
    fetch(`${API_BASE}/products?${params}`)
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json();
      })
      .then((json) => setProducts(json.data?.items ?? []))
      .catch((err) => console.error('[상품 조회 실패]', err))
      .finally(() => setProductsLoading(false));
  };

  useEffect(() => {
    if (!showProductPicker) return;
    fetchProducts(productKeyword);
  }, [showProductPicker]);

  useEffect(() => {
    if (!showProductPicker) return;
    const timer = setTimeout(() => fetchProducts(productKeyword), 300);
    return () => clearTimeout(timer);
  }, [productKeyword]);

  const validate = () => {
    const errs: { title?: string; product?: string } = {};
    if (!title.trim()) errs.title = '방송 제목을 입력해주세요.';
    if (selectedProductIds.length === 0) errs.product = '상품을 1개 이상 선택해주세요.';
    return errs;
  };

  const handleSave = async () => {
    const errs = validate();
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }
    setIsSaving(true);
    setErrors((prev) => ({ ...prev, submit: undefined }));
    try {
      const res = await fetch(`${API_BASE}/campaigns`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          title,
          description,
          scheduledAt: scheduledAt ? new Date(scheduledAt).toISOString().slice(0, 19) : new Date().toISOString().slice(0, 19),
          campaignProducts: selectedProductIds.map((id, idx) => {
            const product = products.find((p) => p.id === id);
            return { productId: id, name: product?.name ?? '', displayOrder: idx + 1 };
          }),
        }),
      });
      if (!res.ok) {
        const body = await res.json().catch(() => null);
        throw new Error(body?.message ?? `등록 실패 (${res.status})`);
      }
      router.push('/broadcasts');
    } catch (err) {
      setErrors((prev) => ({ ...prev, submit: err instanceof Error ? err.message : '등록 중 오류가 발생했습니다.' }));
      setIsSaving(false);
    }
  };

  const toggleProduct = (id: number) => {
    setSelectedProductIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
    setErrors((prev) => ({ ...prev, product: undefined }));
  };

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
        .btn-cancel { padding: 10px 22px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #94a3b8; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: background 0.2s; display: inline-flex; align-items: center; margin-top: 8px; }
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
        .product-section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
        .product-section-label { font-size: 15px; font-weight: 700; color: #f1f5f9; }
        .product-count { display: inline-flex; align-items: center; justify-content: center; width: 20px; height: 20px; background: #6366f1; color: white; border-radius: 50%; font-size: 11px; font-weight: 700; margin-left: 6px; }
        .btn-add-product { padding: 6px 14px; background: rgba(99,102,241,0.15); border: 1px solid rgba(99,102,241,0.3); color: #a5b4fc; border-radius: 7px; font-size: 13px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
        .btn-add-product:hover { background: rgba(99,102,241,0.25); }

        .product-list { display: flex; flex-direction: column; gap: 8px; margin-bottom: 12px; }
        .product-list-card { display: flex; align-items: center; justify-content: space-between; padding: 12px 14px; background: rgba(99,102,241,0.06); border: 1px solid rgba(99,102,241,0.2); border-radius: 10px; }
        .product-list-info { display: flex; align-items: center; gap: 10px; }
        .product-list-name { font-size: 14px; font-weight: 600; color: #f1f5f9; }
        .product-list-cat { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 999px; background: rgba(99,102,241,0.15); color: #a5b4fc; border: 1px solid rgba(99,102,241,0.25); }
        .product-list-price { font-size: 13px; font-weight: 600; color: #94a3b8; }
        .btn-remove-product { background: none; border: none; color: #475569; font-size: 14px; cursor: pointer; padding: 2px 6px; border-radius: 4px; transition: color 0.15s, background 0.15s; }
        .btn-remove-product:hover { color: #f87171; background: rgba(239,68,68,0.1); }
        .product-empty { font-size: 13px; color: #475569; margin-bottom: 12px; }

        .product-search { width: 100%; padding: 9px 12px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; font-size: 13px; color: #f1f5f9; outline: none; font-family: inherit; margin-bottom: 6px; }
        .product-search:focus { border-color: #6366f1; }
        .product-search::placeholder { color: #334155; }
        .product-picker { display: flex; flex-direction: column; gap: 4px; padding: 8px; background: rgba(0,0,0,0.25); border: 1px solid rgba(255,255,255,0.08); border-radius: 10px; }
        .picker-item { display: flex; align-items: center; justify-content: space-between; padding: 9px 12px; border-radius: 8px; cursor: pointer; transition: background 0.15s; }
        .picker-item:hover { background: rgba(255,255,255,0.04); }
        .picker-item.selected { background: rgba(99,102,241,0.1); }
        .picker-item-left { display: flex; align-items: center; gap: 8px; }
        .picker-item-name { font-size: 13px; font-weight: 600; color: #e2e8f0; }
        .picker-item-cat { font-size: 10px; font-weight: 700; padding: 2px 7px; border-radius: 999px; background: rgba(99,102,241,0.12); color: #a5b4fc; }
        .picker-item-right { display: flex; align-items: center; gap: 10px; }
        .picker-item-price { font-size: 12px; color: #64748b; }
        .picker-check { font-size: 12px; font-weight: 700; color: #475569; width: 16px; text-align: center; }
        .picker-check.on { color: #6366f1; }

        .error-msg { font-size: 12px; color: #f87171; margin-top: 8px; }

        @media (max-width: 640px) {
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
            {errors.submit && <span style={{ fontSize: '12px', color: '#f87171', alignSelf: 'center' }}>{errors.submit}</span>}
            <Link href="/broadcasts" className="btn-cancel" style={{ marginTop: '8px'}}>취소</Link>
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
            <div className="product-section-header">
              <span className="product-section-label">
                상품 선택 *
                {selectedProductIds.length > 0 && <span className="product-count">{selectedProductIds.length}</span>}
              </span>
              <button className="btn-add-product" onClick={() => setShowProductPicker((v) => !v)}>
                {showProductPicker ? '닫기' : '+ 상품 추가'}
              </button>
            </div>

            {selectedProductIds.length === 0 ? (
              <div className="product-empty">선택된 상품이 없습니다.</div>
            ) : (
              <div className="product-list">
                {selectedProductIds.map((pid) => {
                  const p = products.find((x) => x.id === pid);
                  if (!p) return null;
                  return (
                    <div key={pid} className="product-list-card">
                      <div className="product-list-info">
                        <span className="product-list-name">{p.name}</span>
                        {p.categoryName && <span className="product-list-cat">{p.categoryName}</span>}
                        <span className="product-list-price">{p.price.toLocaleString()}원</span>
                      </div>
                      <button className="btn-remove-product" onClick={() => toggleProduct(pid)}>✕</button>
                    </div>
                  );
                })}
              </div>
            )}

            {showProductPicker && (
              <>
                <input
                  className="product-search"
                  placeholder="상품명으로 검색..."
                  value={productKeyword}
                  onChange={(e) => setProductKeyword(e.target.value)}
                />
              <div className="product-picker">
                {productsLoading ? (
                  <div style={{ padding: '16px', fontSize: '13px', color: '#475569', textAlign: 'center' }}>검색 중...</div>
                ) : products.length === 0 ? (
                  <div style={{ padding: '16px', fontSize: '13px', color: '#475569', textAlign: 'center' }}>검색 결과가 없습니다.</div>
                ) : products.map((p) => {
                  const isSelected = selectedProductIds.includes(p.id);
                  return (
                    <div
                      key={p.id}
                      className={`picker-item ${isSelected ? 'selected' : ''}`}
                      onClick={() => toggleProduct(p.id)}
                    >
                      <div className="picker-item-left">
                        <span className="picker-item-name">{p.name}</span>
                        {p.categoryName && <span className="picker-item-cat">{p.categoryName}</span>}
                      </div>
                      <div className="picker-item-right">
                        <span className="picker-item-price">{p.price.toLocaleString()}원</span>
                        <span className={`picker-check ${isSelected ? 'on' : ''}`}>{isSelected ? '✓' : '+'}</span>
                      </div>
                    </div>
                  );
                })}
              </div>
              </>
            )}

            {errors.product && <div className="error-msg">{errors.product}</div>}
          </div>
        </div>
      </div>
    </>
  );
}
