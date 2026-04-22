import React, { useState, useRef, useEffect } from 'react';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';

interface ProductForm {
  name: string;
  price: string;
  description: string;
  ingredients: string;
  usage: string;
  manufacturer: string;
}

interface ParsedProductApiResponse {
  name: string | null;
  price: number | null;
  description: string | null;
  manufacturer: string | null;
  ingredients: string | null;
  usageMethod: string | null;
  extractedText: string | null;
}

interface CategoryNode {
  id: number;
  code: string;
  name: string;
  level: number;
  sortOrder: number;
  children: CategoryNode[];
}

const API_BASE = `${process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8090'}/api/v1`;

const EMPTY_FORM: ProductForm = {
  name: '', price: '', description: '',
  ingredients: '', usage: '', manufacturer: '',
};


export default function ProductNew() {
  const router = useRouter();
  const [form, setForm] = useState<ProductForm>(EMPTY_FORM);
  const [errors, setErrors] = useState<Partial<ProductForm> & { category?: string; submit?: string }>({});
  const [pdfFile, setPdfFile] = useState<{ name: string; size: string } | null>(null);
  const [pdfState, setPdfState] = useState<'idle' | 'extracting' | 'done' | 'error'>('idle');
  const [pdfError, setPdfError] = useState<string | null>(null);
  const [extractedText, setExtractedText] = useState<string | null>(null);
  const [pdfFileName, setPdfFileName] = useState<string | null>(null);
  const [isDragOver, setIsDragOver] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // 카테고리
  const [categoryTree, setCategoryTree] = useState<CategoryNode[]>([]);
  const [categoryLoading, setCategoryLoading] = useState(true);

  useEffect(() => {
    fetch(`${API_BASE}/categories`)
      .then((r) => r.json())
      .then((json) => {
        setCategoryTree(json.data.categories);
        setCategoryLoading(false);
      });
  }, []);

  // 카테고리 모달
  const [showCategoryModal, setShowCategoryModal] = useState(false);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(null);
  const [selectedCategoryLabel, setSelectedCategoryLabel] = useState('');
  const [depth1, setDepth1] = useState<CategoryNode | null>(null);
  const [depth2, setDepth2] = useState<CategoryNode | null>(null);

  const set = (key: keyof ProductForm) => (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setForm((prev) => ({ ...prev, [key]: e.target.value }));
    setErrors((prev) => ({ ...prev, [key]: undefined }));
  };

  const handlePdf = async (files: FileList | null) => {
    if (!files || files.length === 0) return;
    const file = files[0];
    if (!file.name.endsWith('.pdf')) return;

    setPdfFile({ name: file.name, size: `${(file.size / 1024 / 1024).toFixed(1)} MB` });
    setPdfState('extracting');
    setPdfError(null);

    try {
      const formData = new FormData();
      formData.append('file', file);

      const res = await fetch(`${API_BASE}/products/pdf/parse`, {
        method: 'POST',
        body: formData,
      });

      if (!res.ok) {
        const body = await res.json().catch(() => null);
        throw new Error(body?.message ?? `PDF 분석 실패 (${res.status})`);
      }

      const json = await res.json();
      const parsed: ParsedProductApiResponse = json.data ?? {};

      setForm((prev) => ({
        name: prev.name || parsed.name || '',
        price: prev.price || (parsed.price != null ? String(parsed.price) : ''),
        description: prev.description || parsed.description || '',
        ingredients: prev.ingredients || parsed.ingredients || '',
        usage: prev.usage || parsed.usageMethod || '',
        manufacturer: prev.manufacturer || parsed.manufacturer || '',
      }));
      setExtractedText(parsed.extractedText ?? null);
      setPdfFileName(file.name);
      setPdfState('done');
    } catch (err) {
      setPdfError(err instanceof Error ? err.message : 'PDF 분석 중 오류가 발생했습니다.');
      setPdfState('error');
    }
  };

  const openCategoryModal = () => {
    setDepth1(null);
    setDepth2(null);
    setShowCategoryModal(true);
  };

  const selectDepth3 = (d1: CategoryNode, d2: CategoryNode, d3: CategoryNode) => {
    setSelectedCategoryId(d3.id);
    setSelectedCategoryLabel(`${d1.name} > ${d2.name} > ${d3.name}`);
    setErrors((prev) => ({ ...prev, category: undefined }));
    setShowCategoryModal(false);
  };

  const validate = () => {
    const errs: Partial<ProductForm> & { category?: string } = {};
    if (!form.name.trim()) errs.name = '상품명을 입력해주세요.';
    if (!selectedCategoryId) errs.category = '카테고리를 선택해주세요.';
    if (!form.price || isNaN(Number(form.price))) errs.price = '올바른 가격을 입력해주세요.';
    if (!form.description.trim()) errs.description = '상품 설명을 입력해주세요.';
    return errs;
  };

  const handleSave = async () => {
    const errs = validate();
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }
    setIsSaving(true);
    setErrors((prev) => ({ ...prev, submit: undefined }));
    try {
      const res = await fetch(`${API_BASE}/products`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: form.name,
          description: form.description,
          price: Number(form.price),
          categoryIds: [selectedCategoryId],
          stockQuantity: 0,
          sortOrder: 0,
          manufacturer: form.manufacturer || null,
          ingredients: form.ingredients || null,
          usageMethod: form.usage || null,
          extractedText: extractedText ?? null,
          pdfFileName: pdfFileName ?? null,
        }),
      });
      if (!res.ok) {
        const body = await res.json().catch(() => null);
        throw new Error(body?.message ?? `등록 실패 (${res.status})`);
      }
      router.push('/products');
    } catch (err) {
      setErrors((prev) => ({ ...prev, submit: err instanceof Error ? err.message : '등록 중 오류가 발생했습니다.' }));
      setIsSaving(false);
    }
  };

  const depth2List = depth1?.children ?? [];
  const depth3List = depth2?.children ?? [];

  return (
    <>
      <Head><title>상품 등록 — Live Platform Lab</title></Head>
      <style jsx global>{`* { margin:0;padding:0;box-sizing:border-box; } body { font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif; background:#0f172a; color:#e2e8f0; min-height:100vh; } a { text-decoration:none;color:inherit; }`}</style>
      <style jsx>{`
        .container { max-width: 1100px; margin: 0 auto; padding: 40px 24px; }
        .nav { display: flex; align-items: center; gap: 8px; margin-bottom: 40px; font-size: 13px; color: #64748b; }
        .nav a:hover { color: #a5b4fc; }
        .nav-sep { color: #334155; }

        .page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 32px; }
        .page-title { font-size: 26px; font-weight: 800; color: #f1f5f9; }
        .header-actions { display: flex; gap: 10px; }
        .btn-cancel { padding: 10px 22px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #94a3b8; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: background 0.2s; text-decoration: none; display: inline-flex; align-items: center; margin-top: 8px; }
        .btn-cancel:hover { background: rgba(255,255,255,0.1); }
        .btn-save { padding: 10px 28px; background: linear-gradient(135deg, #6366f1, #8b5cf6); color: white; border: none; border-radius: 8px; font-size: 14px; font-weight: 700; cursor: pointer; transition: opacity 0.2s; }
        .btn-save:hover:not(:disabled) { opacity: 0.88; }
        .btn-save:disabled { opacity: 0.5; cursor: not-allowed; }

        .layout { display: grid; grid-template-columns: 1fr 340px; gap: 24px; align-items: start; }

        /* 좌측 폼 */
        .form-card { background: #1e293b; border: 1px solid rgba(255,255,255,0.1); border-radius: 16px; padding: 28px; display: flex; flex-direction: column; gap: 20px; }
        .field { display: flex; flex-direction: column; gap: 6px; }
        .field-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
        .label { font-size: 12px; font-weight: 700; color: #64748b; letter-spacing: 0.04em; text-transform: uppercase; }
        .input { width: 100%; padding: 11px 14px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; font-size: 14px; color: #f1f5f9; outline: none; transition: border-color 0.2s, background 0.2s; font-family: inherit; }
        .input:focus { border-color: #6366f1; background: rgba(99,102,241,0.05); }
        .input.error { border-color: rgba(239,68,68,0.5); }
        .input::placeholder { color: #334155; }
        textarea.input { resize: vertical; min-height: 80px; line-height: 1.6; }
        .field-error { font-size: 12px; color: #f87171; }
        .section-divider { height: 1px; background: rgba(255,255,255,0.07); }

        /* 카테고리 선택 버튼 */
        .cat-btn { width: 100%; padding: 11px 14px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; font-size: 14px; color: #f1f5f9; outline: none; cursor: pointer; text-align: left; font-family: inherit; transition: border-color 0.2s, background 0.2s; display: flex; align-items: center; justify-content: space-between; gap: 8px; }
        .cat-btn:hover { border-color: rgba(99,102,241,0.4); background: rgba(99,102,241,0.04); }
        .cat-btn.error { border-color: rgba(239,68,68,0.5); }
        .cat-btn-placeholder { color: #334155; }
        .cat-btn-label { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
        .cat-btn-arrow { color: #475569; font-size: 12px; flex-shrink: 0; }

        /* 카테고리 모달 */
        .modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.65); z-index: 200; display: flex; align-items: center; justify-content: center; }
        .modal { background: #1e293b; border: 1px solid rgba(255,255,255,0.12); border-radius: 16px; width: 560px; max-width: 95vw; overflow: hidden; box-shadow: 0 24px 64px rgba(0,0,0,0.5); }
        .modal-header { padding: 18px 22px; border-bottom: 1px solid rgba(255,255,255,0.08); display: flex; align-items: center; justify-content: space-between; }
        .modal-title { font-size: 15px; font-weight: 700; color: #f1f5f9; }
        .modal-close { background: none; border: none; color: #64748b; font-size: 18px; cursor: pointer; padding: 2px 6px; border-radius: 4px; line-height: 1; }
        .modal-close:hover { color: #94a3b8; background: rgba(255,255,255,0.06); }
        .modal-body { display: grid; grid-template-columns: 1fr 1fr 1fr; height: 300px; }
        .cat-col { border-right: 1px solid rgba(255,255,255,0.07); overflow-y: auto; }
        .cat-col:last-child { border-right: none; }
        .cat-col-head { font-size: 10px; font-weight: 700; color: #475569; padding: 10px 14px 6px; letter-spacing: 0.06em; text-transform: uppercase; border-bottom: 1px solid rgba(255,255,255,0.05); }
        .cat-item { padding: 10px 14px; font-size: 13px; color: #94a3b8; cursor: pointer; transition: background 0.12s, color 0.12s; }
        .cat-item:hover { background: rgba(255,255,255,0.05); color: #e2e8f0; }
        .cat-item.active { background: rgba(99,102,241,0.12); color: #a5b4fc; font-weight: 600; }
        .cat-item.leaf { color: #6ee7b7; }
        .cat-item.leaf:hover { background: rgba(16,185,129,0.08); color: #34d399; }
        .cat-empty { padding: 24px 14px; font-size: 12px; color: #334155; text-align: center; }

        /* 우측 PDF */
        .right { display: flex; flex-direction: column; gap: 16px; position: sticky; top: 24px; }
        .pdf-card { background: #1e293b; border: 1px solid rgba(255,255,255,0.1); border-radius: 16px; padding: 22px; }
        .pdf-title { font-size: 15px; font-weight: 700; color: #f1f5f9; margin-bottom: 4px; }
        .pdf-sub { font-size: 12px; color: #64748b; margin-bottom: 16px; line-height: 1.55; }

        .drop-zone { border: 1.5px dashed rgba(99,102,241,0.35); border-radius: 10px; padding: 28px 20px; text-align: center; cursor: pointer; transition: border-color 0.2s, background 0.2s; }
        .drop-zone:hover, .drop-zone.over { border-color: rgba(99,102,241,0.65); background: rgba(99,102,241,0.06); }
        .drop-icon { font-size: 28px; margin-bottom: 10px; }
        .drop-text { font-size: 13px; color: #64748b; }
        .drop-sub { font-size: 11px; color: #334155; margin-top: 4px; }

        .pdf-file { margin-top: 14px; display: flex; align-items: center; gap: 10px; padding: 10px 14px; background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08); border-radius: 8px; }
        .pdf-file-icon { font-size: 20px; flex-shrink: 0; }
        .pdf-file-info { flex: 1; min-width: 0; }
        .pdf-file-name { font-size: 13px; font-weight: 600; color: #e2e8f0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
        .pdf-file-size { font-size: 11px; color: #475569; margin-top: 1px; }
        .pdf-status { font-size: 11px; font-weight: 700; padding: 3px 9px; border-radius: 999px; flex-shrink: 0; }
        .pdf-extracting { background: rgba(251,191,36,0.12); color: #fcd34d; border: 1px solid rgba(251,191,36,0.25); }
        .pdf-done { background: rgba(16,185,129,0.12); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.25); }
        .pdf-error-badge { background: rgba(239,68,68,0.12); color: #f87171; border: 1px solid rgba(239,68,68,0.25); }

        .extract-notice { margin-top: 12px; padding: 10px 14px; background: rgba(16,185,129,0.06); border: 1px solid rgba(16,185,129,0.15); border-radius: 8px; font-size: 12px; color: #6ee7b7; line-height: 1.55; }

        .hint-card { background: #1e293b; border: 1px solid rgba(255,255,255,0.1); border-radius: 16px; padding: 18px 20px; }
        .hint-title { font-size: 13px; font-weight: 700; color: #94a3b8; margin-bottom: 10px; }
        .hint-item { font-size: 12px; color: #475569; line-height: 1.7; display: flex; gap: 6px; }
        .hint-dot { color: #334155; flex-shrink: 0; }

        @media (max-width: 800px) {
          .layout { grid-template-columns: 1fr; }
          .right { position: static; }
          .field-row { grid-template-columns: 1fr; }
        }
      `}</style>

      {/* 카테고리 선택 모달 */}
      {showCategoryModal && (
        <div className="modal-backdrop" onClick={() => setShowCategoryModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <span className="modal-title">카테고리 선택</span>
              <button className="modal-close" onClick={() => setShowCategoryModal(false)}>✕</button>
            </div>
            <div className="modal-body">
              {/* 1depth */}
              <div className="cat-col">
                <div className="cat-col-head">대분류</div>
                {categoryTree.map((d1) => (
                  <div
                    key={d1.id}
                    className={`cat-item${depth1?.id === d1.id ? ' active' : ''}`}
                    onClick={() => { setDepth1(d1); setDepth2(null); }}
                  >
                    {d1.name}
                  </div>
                ))}
              </div>

              {/* 2depth */}
              <div className="cat-col">
                <div className="cat-col-head">중분류</div>
                {depth1 ? (
                  depth2List.length > 0 ? depth2List.map((d2) => (
                    <div
                      key={d2.id}
                      className={`cat-item${depth2?.id === d2.id ? ' active' : ''}`}
                      onClick={() => setDepth2(d2)}
                    >
                      {d2.name}
                    </div>
                  )) : <div className="cat-empty">하위 카테고리 없음</div>
                ) : <div className="cat-empty">대분류를 선택하세요</div>}
              </div>

              {/* 3depth */}
              <div className="cat-col">
                <div className="cat-col-head">소분류</div>
                {depth2 ? (
                  depth3List.length > 0 ? depth3List.map((d3) => (
                    <div
                      key={d3.id}
                      className="cat-item leaf"
                      onClick={() => selectDepth3(depth1!, depth2!, d3)}
                    >
                      {d3.name}
                    </div>
                  )) : <div className="cat-empty">하위 카테고리 없음</div>
                ) : <div className="cat-empty">중분류를 선택하세요</div>}
              </div>
            </div>
          </div>
        </div>
      )}

      <div className="container">
        <nav className="nav">
          <Link href="/">홈</Link>
          <span className="nav-sep">/</span>
          <Link href="/products">상품 관리</Link>
          <span className="nav-sep">/</span>
          <span style={{ color: '#94a3b8' }}>상품 등록</span>
        </nav>

        <div className="page-header">
          <h1 className="page-title">상품 등록</h1>
          <div className="header-actions">
            {errors.submit && <span className="field-error" style={{ alignSelf: 'center' }}>{errors.submit}</span>}
            <Link href="/products" className="btn-cancel" style={{ marginTop: '8px' }}>취소</Link>
            <button className="btn-save" onClick={handleSave} disabled={isSaving}>
              {isSaving ? '저장 중...' : '저장'}
            </button>
          </div>
        </div>

        <div className="layout">
          {/* 좌측: 입력 폼 */}
          <div className="form-card">
            <div className="field-row">
              <div className="field">
                <label className="label">상품명 *</label>
                <input
                  className={`input ${errors.name ? 'error' : ''}`}
                  placeholder="예) 워터프루프 립스틱"
                  value={form.name}
                  onChange={set('name')}
                />
                {errors.name && <span className="field-error">{errors.name}</span>}
              </div>
              <div className="field">
                <label className="label">카테고리 *</label>
                <button
                  type="button"
                  className={`cat-btn${errors.category ? ' error' : ''}`}
                  onClick={openCategoryModal}
                  disabled={categoryLoading}
                >
                  {selectedCategoryLabel ? (
                    <span className="cat-btn-label">{selectedCategoryLabel}</span>
                  ) : (
                    <span className="cat-btn-placeholder">{categoryLoading ? '카테고리 로딩 중...' : '카테고리 선택'}</span>
                  )}
                  <span className="cat-btn-arrow">▼</span>
                </button>
                {errors.category && <span className="field-error">{errors.category}</span>}
              </div>
            </div>

            <div className="field-row">
              <div className="field">
                <label className="label">가격 (원) *</label>
                <input
                  className={`input ${errors.price ? 'error' : ''}`}
                  type="number"
                  placeholder="예) 25000"
                  value={form.price}
                  onChange={set('price')}
                />
                {errors.price && <span className="field-error">{errors.price}</span>}
              </div>
              <div className="field">
                <label className="label">제조사</label>
                <input
                  className="input"
                  placeholder="예) 뷰티코리아"
                  value={form.manufacturer}
                  onChange={set('manufacturer')}
                />
              </div>
            </div>

            <div className="section-divider" />

            <div className="field">
              <label className="label">상품 설명 *</label>
              <textarea
                className={`input ${errors.description ? 'error' : ''}`}
                placeholder="상품의 특징과 효과를 간결하게 입력하세요."
                value={form.description}
                onChange={set('description')}
                rows={3}
              />
              {errors.description && <span className="field-error">{errors.description}</span>}
            </div>

            <div className="field">
              <label className="label">주요 성분</label>
              <textarea
                className="input"
                placeholder="예) 정제수, 글리세린, 나이아신아마이드, 히알루론산..."
                value={form.ingredients}
                onChange={set('ingredients')}
                rows={2}
              />
            </div>

            <div className="field">
              <label className="label">사용법</label>
              <textarea
                className="input"
                placeholder="사용 방법과 주의사항을 입력하세요."
                value={form.usage}
                onChange={set('usage')}
                rows={2}
              />
            </div>
          </div>

          {/* 우측: PDF 업로드 */}
          <div className="right">
            <div className="pdf-card">
              <div className="pdf-title">PDF로 자동 입력</div>
              <div className="pdf-sub">상품 설명서나 성분표 PDF를 업로드하면 AI가 내용을 분석해 자동으로 채워드립니다.</div>

              <div
                className={`drop-zone ${isDragOver ? 'over' : ''}`}
                onClick={() => fileInputRef.current?.click()}
                onDragOver={(e) => { e.preventDefault(); setIsDragOver(true); }}
                onDragLeave={() => setIsDragOver(false)}
                onDrop={(e) => { e.preventDefault(); setIsDragOver(false); handlePdf(e.dataTransfer.files); }}
              >
                <div className="drop-icon">📄</div>
                <div className="drop-text">PDF를 드래그하거나 클릭하여 업로드</div>
                <div className="drop-sub">AI가 상품 정보를 자동으로 추출합니다</div>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept=".pdf"
                  style={{ display: 'none' }}
                  onChange={(e) => handlePdf(e.target.files)}
                />
              </div>

              {pdfFile && (
                <>
                  <div className="pdf-file">
                    <span className="pdf-file-icon">📄</span>
                    <div className="pdf-file-info">
                      <div className="pdf-file-name">{pdfFile.name}</div>
                      <div className="pdf-file-size">{pdfFile.size}</div>
                    </div>
                    <span className={`pdf-status ${pdfState === 'extracting' ? 'pdf-extracting' : pdfState === 'error' ? 'pdf-error-badge' : 'pdf-done'}`}>
                      {pdfState === 'extracting' ? '분석 중...' : pdfState === 'error' ? '실패' : '완료'}
                    </span>
                  </div>
                  {pdfState === 'done' && (
                    <div className="extract-notice">
                      PDF에서 상품 정보를 추출했습니다. 내용을 확인하고 필요하면 수정해주세요.
                    </div>
                  )}
                  {pdfState === 'error' && pdfError && (
                    <div style={{ marginTop: '12px', padding: '10px 14px', background: 'rgba(239,68,68,0.06)', border: '1px solid rgba(239,68,68,0.15)', borderRadius: '8px', fontSize: '12px', color: '#f87171', lineHeight: 1.55 }}>
                      {pdfError}
                    </div>
                  )}
                </>
              )}
            </div>

            <div className="hint-card">
              <div className="hint-title">입력 가이드</div>
              <div className="hint-item"><span className="hint-dot">·</span>상품 설명은 AI Q&A 답변의 주요 근거로 활용됩니다.</div>
              <div className="hint-item"><span className="hint-dot">·</span>성분 정보를 상세히 입력할수록 정확한 답변이 가능합니다.</div>
              <div className="hint-item"><span className="hint-dot">·</span>저장 후 상품 상세 페이지에서 추가 문서를 업로드할 수 있습니다.</div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
