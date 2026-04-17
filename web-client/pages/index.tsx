import React from 'react';
import Head from 'next/head';
import Link from 'next/link';

const features = [
  {
    icon: '📄',
    title: 'PDF 상품 임베딩',
    description: '상품 정보 PDF를 업로드하면 자동으로 벡터화하여 AI 검색에 활용합니다.',
    status: 'planned',
  },
  {
    icon: '🤖',
    title: 'AI 고객 Q&A',
    description: '방송 중 고객 질문에 RAG 기반으로 상품 정보를 근거로 한 답변을 제공합니다.',
    status: 'planned',
  },
  {
    icon: '💬',
    title: '실시간 채팅',
    description: 'WebSocket/STOMP 기반의 라이브 채팅으로 고객과 실시간으로 소통합니다.',
    status: 'done',
  },
  {
    icon: '📊',
    title: '호스트 인사이트',
    description: '채팅 메시지를 분석하여 호스트에게 실시간 반응 및 인사이트를 제공합니다.',
    status: 'planned',
  },
];

const techStack = [
  { name: 'Spring Boot', desc: 'Java 백엔드' },
  { name: 'Spring AI', desc: 'LLM / RAG' },
  { name: 'ElasticSearch', desc: '벡터 + 키워드 검색' },
  { name: 'STOMP / WebSocket', desc: '실시간 채팅' },
  { name: 'Redis', desc: '캐싱' },
  { name: 'Next.js', desc: '프론트엔드' },
];

export default function Home() {
  return (
    <>
      <Head>
        <title>Live Platform Lab</title>
        <meta name="description" content="AI 기반 라이브 커머스 플랫폼" />
      </Head>

      <style jsx global>{`
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
          background: #0f172a;
          color: #e2e8f0;
          min-height: 100vh;
        }
        a { text-decoration: none; color: inherit; }
      `}</style>

      <style jsx>{`
        .container {
          max-width: 1100px;
          margin: 0 auto;
          padding: 60px 24px;
        }

        /* Hero */
        .hero {
          text-align: center;
          margin-bottom: 80px;
        }
        .hero-badge {
          display: inline-block;
          background: rgba(99, 102, 241, 0.15);
          border: 1px solid rgba(99, 102, 241, 0.4);
          color: #a5b4fc;
          font-size: 13px;
          font-weight: 600;
          padding: 6px 16px;
          border-radius: 999px;
          margin-bottom: 24px;
          letter-spacing: 0.05em;
        }
        .hero h1 {
          font-size: 52px;
          font-weight: 800;
          line-height: 1.15;
          margin-bottom: 20px;
          background: linear-gradient(135deg, #e2e8f0 0%, #a5b4fc 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
        .hero p {
          font-size: 18px;
          color: #94a3b8;
          max-width: 560px;
          margin: 0 auto 40px;
          line-height: 1.7;
        }
        .hero-actions {
          display: flex;
          gap: 16px;
          justify-content: center;
          flex-wrap: wrap;
        }
        .btn-primary {
          padding: 14px 32px;
          background: linear-gradient(135deg, #6366f1, #8b5cf6);
          color: white;
          border-radius: 10px;
          font-size: 15px;
          font-weight: 600;
          transition: opacity 0.2s, transform 0.2s;
        }
        .btn-primary:hover {
          opacity: 0.9;
          transform: translateY(-2px);
        }
        .btn-secondary {
          padding: 14px 32px;
          background: rgba(255, 255, 255, 0.05);
          border: 1px solid rgba(255, 255, 255, 0.12);
          color: #cbd5e1;
          border-radius: 10px;
          font-size: 15px;
          font-weight: 600;
          transition: background 0.2s, transform 0.2s;
        }
        .btn-secondary:hover {
          background: rgba(255, 255, 255, 0.1);
          transform: translateY(-2px);
        }

        /* Section title */
        .section-title {
          font-size: 13px;
          font-weight: 700;
          letter-spacing: 0.1em;
          color: #6366f1;
          text-transform: uppercase;
          margin-bottom: 12px;
        }
        .section-heading {
          font-size: 28px;
          font-weight: 700;
          color: #f1f5f9;
          margin-bottom: 40px;
        }

        /* Features */
        .features-section {
          margin-bottom: 80px;
        }
        .features-grid {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 20px;
        }
        .feature-card {
          background: rgba(255, 255, 255, 0.04);
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: 14px;
          padding: 28px;
          transition: border-color 0.2s, background 0.2s;
        }
        .feature-card:hover {
          border-color: rgba(99, 102, 241, 0.4);
          background: rgba(99, 102, 241, 0.05);
        }
        .feature-header {
          display: flex;
          align-items: center;
          gap: 14px;
          margin-bottom: 12px;
        }
        .feature-icon {
          font-size: 28px;
        }
        .feature-title {
          font-size: 17px;
          font-weight: 700;
          color: #f1f5f9;
        }
        .feature-desc {
          font-size: 14px;
          color: #94a3b8;
          line-height: 1.65;
        }
        .status-badge {
          display: inline-block;
          font-size: 11px;
          font-weight: 600;
          padding: 3px 10px;
          border-radius: 999px;
          margin-top: 14px;
        }
        .status-done {
          background: rgba(16, 185, 129, 0.15);
          color: #6ee7b7;
          border: 1px solid rgba(16, 185, 129, 0.3);
        }
        .status-planned {
          background: rgba(99, 102, 241, 0.12);
          color: #a5b4fc;
          border: 1px solid rgba(99, 102, 241, 0.25);
        }

        /* Tech stack */
        .tech-section {
          margin-bottom: 80px;
        }
        .tech-grid {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 14px;
        }
        .tech-item {
          background: rgba(255, 255, 255, 0.04);
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: 10px;
          padding: 16px 20px;
        }
        .tech-name {
          font-size: 14px;
          font-weight: 700;
          color: #e2e8f0;
          margin-bottom: 4px;
        }
        .tech-desc {
          font-size: 12px;
          color: #64748b;
        }

        /* Dev links */
        .dev-section {
          border-top: 1px solid rgba(255, 255, 255, 0.07);
          padding-top: 40px;
        }
        .dev-links {
          display: flex;
          gap: 14px;
          flex-wrap: wrap;
        }
        .dev-link {
          display: flex;
          align-items: center;
          gap: 10px;
          background: rgba(255, 255, 255, 0.04);
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: 10px;
          padding: 14px 20px;
          font-size: 14px;
          font-weight: 600;
          color: #94a3b8;
          transition: border-color 0.2s, color 0.2s;
        }
        .dev-link:hover {
          border-color: rgba(99, 102, 241, 0.5);
          color: #a5b4fc;
        }

        @media (max-width: 640px) {
          .hero h1 { font-size: 34px; }
          .features-grid { grid-template-columns: 1fr; }
          .tech-grid { grid-template-columns: repeat(2, 1fr); }
        }
      `}</style>

      <div className="container">
        {/* Hero */}
        <section className="hero">
          <div className="hero-badge">AI × Live Commerce</div>
          <h1>Live Platform Lab</h1>
          <p>
            상품 PDF 임베딩부터 실시간 AI 답변, 호스트 인사이트까지 —
            라이브 커머스를 위한 AI 플랫폼 실험실입니다.
          </p>
          <div className="hero-actions">
            <Link href="/broadcasts" className="btn-primary">
              📡 방송 목록 보기
            </Link>
            <a
              href="https://github.com/kjuiop/live-platform-lab"
              target="_blank"
              rel="noreferrer"
              className="btn-secondary"
            >
              GitHub
            </a>
          </div>
        </section>

        {/* Features */}
        <section className="features-section">
          <div className="section-title">Features</div>
          <div className="section-heading">주요 기능</div>
          <div className="features-grid">
            {features.map((f) => (
              <div key={f.title} className="feature-card">
                <div className="feature-header">
                  <span className="feature-icon">{f.icon}</span>
                  <span className="feature-title">{f.title}</span>
                </div>
                <p className="feature-desc">{f.description}</p>
                <span className={`status-badge ${f.status === 'done' ? 'status-done' : 'status-planned'}`}>
                  {f.status === 'done' ? '구현 완료' : '개발 예정'}
                </span>
              </div>
            ))}
          </div>
        </section>

        {/* Tech Stack */}
        <section className="tech-section">
          <div className="section-title">Stack</div>
          <div className="section-heading">기술 스택</div>
          <div className="tech-grid">
            {techStack.map((t) => (
              <div key={t.name} className="tech-item">
                <div className="tech-name">{t.name}</div>
                <div className="tech-desc">{t.desc}</div>
              </div>
            ))}
          </div>
        </section>

        {/* Dev Links */}
        <section className="dev-section">
          <div className="section-title">Dev</div>
          <div className="section-heading">개발 도구</div>
          <div className="dev-links">
            <Link href="/chat" className="dev-link">
              💬 채팅방 데모
            </Link>
            <Link href="/stomp-test" className="dev-link">
              🔌 STOMP 채팅 테스트
            </Link>
          </div>
        </section>
      </div>
    </>
  );
}
