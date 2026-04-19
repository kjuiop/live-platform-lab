import React, { useState, useRef } from 'react';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';

interface ProductForm {
  name: string;
  category: string;
  price: string;
  description: string;
  ingredients: string;
  usage: string;
  manufacturer: string;
}

const EMPTY_FORM: ProductForm = {
  name: '', category: '', price: '', description: '',
  ingredients: '', usage: '', manufacturer: '',
};

// PDF 업로드 시 AI 추출 시뮬레이션 데이터
const MOCK_PDF_EXTRACTED: Record<string, Partial<ProductForm>> = {
  default: {
    name: '모이스처라이징 선크림',
    category: '스킨케어',
    price: '32000',
    description: 'SPF50+ PA++++ 자외선 차단과 동시에 피부 보습을 케어하는 멀티 기능성 선크림입니다.',
    ingredients: '정제수, 이산화티탄, 징크옥사이드, 글리세린, 나이아신아마이드, 히알루론산나트륨, 판테놀',
    usage: '외출 30분 전 피부 마지막 단계에 적당량을 얼굴 전체에 고르게 펴 바르세요. 2~3시간마다 덧바르는 것을 권장합니다.',
    manufacturer: '스킨랩',
  },
};

export default function ProductNew() {
  const router = useRouter();
  const [form, setForm] = useState<ProductForm>(EMPTY_FORM);
  const [errors, setErrors] = useState<Partial<ProductForm>>({});
  const [pdfFile, setPdfFile] = useState<{ name: string; size: string } | null>(null);
  const [pdfState, setPdfState] = useState<'idle' | 'extracting' | 'done'>('idle');
  const [isDragOver, setIsDragOver] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const set = (key: keyof ProductForm) => (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    setForm((prev) => ({ ...prev, [key]: e.target.value }));
    setErrors((prev) => ({ ...prev, [key]: undefined }));
  };

  const handlePdf = (files: FileList | null) => {
    if (!files || files.length === 0) return;
    const file = files[0];
    if (!file.name.endsWith('.pdf')) return;

    setPdfFile({ name: file.name, size: `${(file.size / 1024 / 1024).toFixed(1)} MB` });
    setPdfState('extracting');

    // AI 추출 시뮬레이션
    setTimeout(() => {
      const extracted = MOCK_PDF_EXTRACTED.default;
      // 빈 필드만 채우기
      setForm((prev) => ({
        name: prev.name || extracted.name || '',
        category: prev.category || extracted.category || '',
        price: prev.price || extracted.price || '',
        description: prev.description || extracted.description || '',
        ingredients: prev.ingredients || extracted.ingredients || '',
        usage: prev.usage || extracted.usage || '',
        manufacturer: prev.manufacturer || extracted.manufacturer || '',
      }));
      setPdfState('done');
    }, 1800);
  };

  const validate = () => {
    const errs: Partial<ProductForm> = {};
    if (!form.name.trim()) errs.name = '상품명을 입력해주세요.';
    if (!form.category.trim()) errs.category = '카테고리를 입력해주세요.';
    if (!form.price || isNaN(Number(form.price))) errs.price = '올바른 가격을 입력해주세요.';
    if (!form.description.trim()) errs.description = '상품 설명을 입력해주세요.';
    return errs;
  };

  const handleSave = async () => {
    const errs = validate();
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }
    setIsSaving(true);
    try {
      const res = await fetch('http://localhost:8090/api/v1/products', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: form.name,
          description: form.description,
          price: Number(form.price),
          categoryIds: [1],
          stockQuantity: 0,
          sortOrder: 0,
          manufacturer: form.manufacturer || null,
          ingredients: form.ingredients || null,
          usageMethod: form.usage || null,
        }),
      });
      if (!res.ok) throw new Error('등록 실패');
      router.push('/products');
    } catch {
      setIsSaving(false);
    }
  };

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
        .btn-cancel { padding: 10px 22px; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #94a3b8; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; transition: background 0.2s; text-decoration: none; display: inline-flex; align-items: center; }
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
            <Link href="/products" className="btn-cancel">취소</Link>
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
                <input
                  className={`input ${errors.category ? 'error' : ''}`}
                  placeholder="예) 뷰티, 스킨케어"
                  value={form.category}
                  onChange={set('category')}
                />
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
                    <span className={`pdf-status ${pdfState === 'extracting' ? 'pdf-extracting' : 'pdf-done'}`}>
                      {pdfState === 'extracting' ? '분석 중...' : '완료'}
                    </span>
                  </div>
                  {pdfState === 'done' && (
                    <div className="extract-notice">
                      PDF에서 상품 정보를 추출했습니다. 내용을 확인하고 필요하면 수정해주세요.
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
