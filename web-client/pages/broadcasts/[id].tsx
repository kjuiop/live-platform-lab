import React, { useState, useRef, useEffect } from 'react';
import Head from 'next/head';
import Link from 'next/link';
import Script from 'next/script';
import { useRouter } from 'next/router';

type BroadcastStatus = 'scheduled' | 'live' | 'ended';

interface CampaignProduct {
  productId: number;
  name: string;
  displayOrder: number;
}

interface Campaign {
  id: number;
  title: string;
  description: string;
  status: string;
  scheduledAt: string | null;
  startedAt: string | null;
  endedAt: string | null;
  campaignProducts: CampaignProduct[];
}

function toUiStatus(apiStatus: string): BroadcastStatus {
  if (apiStatus === 'ON_AIR') return 'live';
  if (apiStatus === 'ENDED') return 'ended';
  return 'scheduled';
}

interface Message {
  id: number;
  nickname: string;
  text: string;
  timestamp: Date;
}

interface QnAItem {
  id: number;
  question: string;
  answer: string;
  askedBy?: string;
  timestamp: Date;
  isLoading?: boolean;
}

// Mock AI 응답
const MOCK_AI_ANSWERS: Record<string, string> = {
  default: '해당 상품에 대한 정보를 분석 중입니다. 잠시 후 답변을 제공해드릴게요.',
  '성분': '이 제품은 피부에 자극이 적은 성분으로 구성되어 있으며, 피부과 테스트를 완료했습니다.',
  '지속력': '워터프루프 포뮬러로 최대 12시간 지속됩니다. 물이나 땀에도 번짐 없이 유지됩니다.',
  '발색': '한 번만 발라도 선명한 발색이 가능하며, 레이어링 시 더욱 진하게 연출할 수 있습니다.',
  '가격': '정상가 28,000원이며, 방송 기간 한정으로 특가 할인이 적용됩니다.',
  '배송': '오늘 주문 시 내일 오후까지 배송 완료됩니다. 5만원 이상 무료배송입니다.',
};

const MOCK_LIVE_FAQS: QnAItem[] = [
  { id: 1, question: '방수 기능이 진짜 있나요?', answer: '', askedBy: '뷰티러버', timestamp: new Date(Date.now() - 8 * 60000) },
  { id: 2, question: '컬러가 몇 가지나 있어요?', answer: '', askedBy: '핑크사랑', timestamp: new Date(Date.now() - 5 * 60000) },
  { id: 3, question: '민감한 피부도 사용 가능한가요?', answer: '', askedBy: '피부걱정', timestamp: new Date(Date.now() - 3 * 60000) },
  { id: 4, question: '맥 립스틱이랑 비교하면 어때요?', answer: '', askedBy: '화장품덕후', timestamp: new Date(Date.now() - 1 * 60000) },
];

const MOCK_AI_ANALYSIS = {
  peakViewers: 1892,
  totalMessages: 4231,
  avgWatchTime: '18분 32초',
  sentiment: { positive: 72, neutral: 20, negative: 8 },
  topKeywords: ['발색', '지속력', '가격', '방수', '향기', '촉촉함'],
  topQuestions: [
    '다른 컬러도 있나요?',
    '민감성 피부도 사용 가능한가요?',
    '방수 기능이 얼마나 지속되나요?',
  ],
  insights: [
    '시청자의 72%가 발색력에 긍정적인 반응을 보였습니다.',
    '"방수" 키워드 언급이 전체 메시지의 18%를 차지해 주요 관심 포인트로 확인됐습니다.',
    '방송 시작 후 8분~12분 구간에서 채팅 참여율이 최고조에 달했습니다.',
    '가격 관련 질문이 많아 다음 방송 시 가격 혜택을 초반에 강조하는 것을 추천합니다.',
  ],
};

const statusLabel: Record<BroadcastStatus, string> = {
  scheduled: '예정',
  live: '라이브 중',
  ended: '종료',
};

// ─── 예정/라이브 공용: 채팅 + Q&A 탭 패널 ───────────────────────────
function ChatQnAPanel({
  status,
  productName,
  messages,
  inputValue,
  setInputValue,
  nickname,
  setNickname,
  onSend,
  wsConnected,
  onToggleWs,
  areScriptsReady,
}: {
  status: BroadcastStatus;
  productName: string;
  messages: Message[];
  inputValue: string;
  setInputValue: (v: string) => void;
  nickname: string;
  setNickname: (v: string) => void;
  onSend: () => void;
  wsConnected: boolean;
  onToggleWs: () => void;
  areScriptsReady: boolean;
}) {
  const isScheduled = status === 'scheduled';
  const [tab, setTab] = useState<'chat' | 'faq'>('chat');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const faqBottomRef = useRef<HTMLDivElement>(null);
  const [liveFaqs] = useState<QnAItem[]>(MOCK_LIVE_FAQS);

  // 예정 탭: AI Q&A 상태
  const [aiInput, setAiInput] = useState('');
  const [aiQnaList, setAiQnaList] = useState<QnAItem[]>([
    {
      id: 1,
      question: '이 제품은 어떤 피부 타입에 적합한가요?',
      answer: '건성, 지성, 복합성 모든 피부 타입에 사용 가능합니다. 특히 수분 밸런스를 유지하는 포뮬러로 구성되어 있습니다.',
      timestamp: new Date(Date.now() - 30 * 60000),
    },
    {
      id: 2,
      question: '발색이 자연스러운가요, 선명한가요?',
      answer: '레이어링 방법에 따라 조절 가능합니다. 한 번 발랐을 때는 데일리로 쓰기 좋은 자연스러운 발색이고, 두 번 이상 발랐을 때는 선명한 컬러가 연출됩니다.',
      timestamp: new Date(Date.now() - 15 * 60000),
    },
  ]);

  useEffect(() => {
    if (tab === 'chat') messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, tab]);

  useEffect(() => {
    if (tab === 'faq') faqBottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [aiQnaList, tab]);

  const handleKey = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') onSend();
  };

  const askAI = () => {
    const q = aiInput.trim();
    if (!q) return;
    setAiInput('');
    const newItem: QnAItem = { id: Date.now(), question: q, answer: '', timestamp: new Date(), isLoading: true };
    setAiQnaList((prev) => [...prev, newItem]);
    const keyword = Object.keys(MOCK_AI_ANSWERS).find((k) => k !== 'default' && q.includes(k));
    const answer = MOCK_AI_ANSWERS[keyword ?? 'default'];
    setTimeout(() => {
      setAiQnaList((prev) => prev.map((item) => item.id === newItem.id ? { ...item, answer, isLoading: false } : item));
    }, 1200);
  };

  return (
    <div className="lp">
      <div className="lp-header">
        <div className="lp-tabs">
          <button className={`lp-tab ${tab === 'chat' ? 'active' : ''}`} onClick={() => setTab('chat')}>
            💬 채팅
          </button>
          <button className={`lp-tab ${tab === 'faq' ? 'active' : ''}`} onClick={() => setTab('faq')}>
            {isScheduled ? '🤖 AI Q&A' : '❓ Q&A 모음'}
            {!isScheduled && <span className="faq-count">{liveFaqs.length}</span>}
          </button>
        </div>
        {!isScheduled && (
          <button
            className={`ws-btn ${wsConnected ? 'connected' : 'disconnected'}`}
            onClick={onToggleWs}
            disabled={!areScriptsReady && !wsConnected}
          >
            {wsConnected ? '● 연결됨' : '연결'}
          </button>
        )}
      </div>

      {tab === 'chat' ? (
        <>
          {isScheduled && (
            <div className="lp-rehearsal-banner">🎬 리허설 모드 · 방송 시작 전입니다</div>
          )}
          <div className="lp-messages">
            {messages.length === 0 ? (
              <div className="lp-empty">{isScheduled ? '리허설 채팅을 시작해보세요!' : '채팅을 시작해보세요!'}</div>
            ) : (
              messages.map((msg) => (
                <div key={msg.id} className="msg">
                  <div className="msg-header">
                    <span className="msg-nick">{msg.nickname}</span>
                    <span className="msg-time">{msg.timestamp.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })}</span>
                  </div>
                  <div className="msg-text">{msg.text}</div>
                </div>
              ))
            )}
            <div ref={messagesEndRef} />
          </div>
          <div className="lp-nick-row">
            <span className="lp-nick-label">닉네임</span>
            <input className="lp-nick-input" value={nickname} onChange={(e) => setNickname(e.target.value)} maxLength={20} />
          </div>
          <div className="lp-input-row">
            <input
              className="lp-input"
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onKeyPress={handleKey}
              placeholder={isScheduled ? '리허설 메시지를 입력하세요...' : '메시지를 입력하세요...'}
            />
            <button className="lp-send" onClick={onSend} disabled={!inputValue.trim()}>전송</button>
          </div>
        </>
      ) : isScheduled ? (
        /* 예정: AI Q&A 탭 */
        <>
          <div className="lp-faq-body">
            <div className="lp-faq-info">방송 전 궁금한 점을 AI에게 물어보세요.</div>
            {aiQnaList.map((item) => (
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
            ))}
            <div ref={faqBottomRef} />
          </div>
          <div className="lp-input-row">
            <input
              className="lp-input"
              value={aiInput}
              onChange={(e) => setAiInput(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && askAI()}
              placeholder={`${productName}에 대해 질문해보세요...`}
            />
            <button className="lp-send ai" onClick={askAI} disabled={!aiInput.trim()}>질문</button>
          </div>
        </>
      ) : (
        /* 라이브: Q&A 모음 탭 */
        <div className="lp-faq-body">
          <div className="lp-faq-info">시청자들이 방송 중 남긴 질문들을 모아볼 수 있어요.</div>
          {liveFaqs.map((item) => (
            <div key={item.id} className="faq-card">
              <div className="faq-card-top">
                <span className="faq-asker">👤 {item.askedBy}</span>
                <span className="faq-time">{item.timestamp.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })}</span>
              </div>
              <div className="faq-q-text">{item.question}</div>
            </div>
          ))}
          <div ref={faqBottomRef} />
        </div>
      )}

      <style jsx>{`
        .lp { display: flex; flex-direction: column; height: 100%; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.08); border-radius: 14px; overflow: hidden; }
        .lp-header { padding: 12px 16px; border-bottom: 1px solid rgba(255,255,255,0.07); display: flex; align-items: center; gap: 8px; }
        .lp-tabs { flex: 1; display: flex; gap: 4px; }
        .lp-tab { padding: 6px 14px; border-radius: 8px; font-size: 13px; font-weight: 600; border: none; cursor: pointer; color: #64748b; background: transparent; display: flex; align-items: center; gap: 6px; transition: all 0.15s; }
        .lp-tab.active { background: rgba(99,102,241,0.15); color: #a5b4fc; }
        .lp-tab:hover:not(.active) { color: #94a3b8; }
        .faq-count { background: rgba(239,68,68,0.2); color: #fca5a5; font-size: 10px; font-weight: 700; padding: 1px 6px; border-radius: 999px; }
        .ws-btn { font-size: 11px; font-weight: 600; padding: 4px 10px; border-radius: 999px; border: none; cursor: pointer; transition: opacity 0.2s; flex-shrink: 0; }
        .ws-btn.connected { background: rgba(16,185,129,0.15); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.3); }
        .ws-btn.disconnected { background: rgba(99,102,241,0.12); color: #a5b4fc; border: 1px solid rgba(99,102,241,0.25); }
        .ws-btn:hover { opacity: 0.8; }
        .lp-messages { flex: 1; overflow-y: auto; padding: 16px; display: flex; flex-direction: column; gap: 10px; scrollbar-width: thin; scrollbar-color: rgba(255,255,255,0.1) transparent; }
        .lp-messages::-webkit-scrollbar { width: 4px; }
        .lp-messages::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 2px; }
        .lp-empty { color: #475569; font-size: 13px; text-align: center; margin-top: 40px; }
        .lp-rehearsal-banner { padding: 7px 16px; background: rgba(251,191,36,0.08); border-bottom: 1px solid rgba(251,191,36,0.15); font-size: 11px; font-weight: 600; color: #fcd34d; text-align: center; letter-spacing: 0.02em; }
        .msg { display: flex; flex-direction: column; gap: 2px; }
        .msg-header { display: flex; align-items: center; gap: 6px; }
        .msg-nick { font-size: 12px; font-weight: 700; color: #818cf8; }
        .msg-time { font-size: 10px; color: #475569; }
        .msg-text { font-size: 13px; color: #cbd5e1; line-height: 1.5; word-break: break-word; padding: 6px 10px; background: rgba(255,255,255,0.04); border-radius: 6px; }
        .lp-nick-row { padding: 10px 16px; border-top: 1px solid rgba(255,255,255,0.06); display: flex; align-items: center; gap: 8px; }
        .lp-nick-label { font-size: 11px; color: #64748b; white-space: nowrap; }
        .lp-nick-input { flex: 1; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.08); border-radius: 6px; padding: 5px 10px; font-size: 12px; color: #f1f5f9; outline: none; }
        .lp-nick-input:focus { border-color: #6366f1; }
        .lp-input-row { padding: 12px 16px; border-top: 1px solid rgba(255,255,255,0.07); display: flex; gap: 8px; }
        .lp-input { flex: 1; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.08); border-radius: 8px; padding: 10px 14px; font-size: 13px; color: #f1f5f9; outline: none; }
        .lp-input:focus { border-color: #6366f1; }
        .lp-send { padding: 10px 16px; background: linear-gradient(135deg, #6366f1, #8b5cf6); color: white; border: none; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; }
        .lp-send.ai { background: linear-gradient(135deg, #10b981, #059669); }
        .lp-send:disabled { opacity: 0.4; cursor: not-allowed; }
        .lp-faq-body { flex: 1; overflow-y: auto; padding: 16px; display: flex; flex-direction: column; gap: 10px; scrollbar-width: thin; scrollbar-color: rgba(255,255,255,0.1) transparent; }
        .lp-faq-body::-webkit-scrollbar { width: 4px; }
        .lp-faq-body::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 2px; }
        .lp-faq-info { font-size: 12px; color: #475569; padding: 8px 12px; background: rgba(255,255,255,0.03); border-radius: 8px; text-align: center; }
        .faq-card { padding: 12px 14px; background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.07); border-radius: 10px; display: flex; flex-direction: column; gap: 6px; }
        .faq-card-top { display: flex; align-items: center; justify-content: space-between; }
        .faq-asker { font-size: 11px; font-weight: 600; color: #818cf8; }
        .faq-time { font-size: 10px; color: #475569; }
        .faq-q-text { font-size: 13px; color: #cbd5e1; line-height: 1.55; }
        .qna-item { display: flex; flex-direction: column; gap: 8px; padding: 12px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 10px; }
        .qna-row { display: flex; align-items: flex-start; gap: 8px; }
        .qna-badge { font-size: 10px; font-weight: 700; padding: 2px 7px; border-radius: 999px; flex-shrink: 0; margin-top: 2px; }
        .qna-badge.q { background: rgba(99,102,241,0.2); color: #a5b4fc; border: 1px solid rgba(99,102,241,0.3); }
        .qna-badge.a { background: rgba(16,185,129,0.15); color: #6ee7b7; border: 1px solid rgba(16,185,129,0.25); }
        .qna-text { font-size: 13px; color: #cbd5e1; line-height: 1.6; }
        .qna-text.answer { color: #94a3b8; }
        .qna-loading { font-size: 13px; color: #6ee7b7; display: flex; align-items: center; gap: 4px; }
        .dots::after { content: '...'; animation: dotanim 1.2s steps(4, end) infinite; }
        @keyframes dotanim { 0%,100% { content: ''; } 25% { content: '.'; } 50% { content: '..'; } 75% { content: '...'; } }
      `}</style>
    </div>
  );
}

// ─── 종료: AI 분석 패널 ──────────────────────────────────────────────
function EndedAnalysisPanel() {
  const a = MOCK_AI_ANALYSIS;
  return (
    <div className="ap">
      <div className="ap-header">
        <span className="ap-icon">📊</span>
        <div>
          <div className="ap-title">AI 방송 분석 리포트</div>
          <div className="ap-sub">방송 종료 후 AI가 자동 분석한 결과입니다</div>
        </div>
      </div>
      <div className="ap-body">
        {/* 주요 지표 */}
        <div className="ap-section">
          <div className="ap-section-title">주요 지표</div>
          <div className="ap-metrics">
            <div className="ap-metric">
              <div className="ap-metric-val">{a.peakViewers.toLocaleString()}</div>
              <div className="ap-metric-label">최고 시청자</div>
            </div>
            <div className="ap-metric">
              <div className="ap-metric-val">{a.totalMessages.toLocaleString()}</div>
              <div className="ap-metric-label">총 채팅 수</div>
            </div>
            <div className="ap-metric">
              <div className="ap-metric-val">{a.avgWatchTime}</div>
              <div className="ap-metric-label">평균 시청 시간</div>
            </div>
          </div>
        </div>

        {/* 감정 분석 */}
        <div className="ap-section">
          <div className="ap-section-title">시청자 반응 분석</div>
          <div className="ap-sentiment-bars">
            <div className="ap-sent-row">
              <span className="ap-sent-label positive">긍정</span>
              <div className="ap-bar-wrap">
                <div className="ap-bar" style={{ width: `${a.sentiment.positive}%`, background: 'linear-gradient(90deg, #10b981, #34d399)' }} />
              </div>
              <span className="ap-sent-pct">{a.sentiment.positive}%</span>
            </div>
            <div className="ap-sent-row">
              <span className="ap-sent-label neutral">중립</span>
              <div className="ap-bar-wrap">
                <div className="ap-bar" style={{ width: `${a.sentiment.neutral}%`, background: 'linear-gradient(90deg, #6366f1, #818cf8)' }} />
              </div>
              <span className="ap-sent-pct">{a.sentiment.neutral}%</span>
            </div>
            <div className="ap-sent-row">
              <span className="ap-sent-label negative">부정</span>
              <div className="ap-bar-wrap">
                <div className="ap-bar" style={{ width: `${a.sentiment.negative}%`, background: 'linear-gradient(90deg, #ef4444, #f87171)' }} />
              </div>
              <span className="ap-sent-pct">{a.sentiment.negative}%</span>
            </div>
          </div>
        </div>

        {/* 주요 키워드 */}
        <div className="ap-section">
          <div className="ap-section-title">주요 키워드</div>
          <div className="ap-keywords">
            {a.topKeywords.map((kw, i) => (
              <span key={kw} className="ap-keyword" style={{ opacity: 1 - i * 0.1 }}>#{kw}</span>
            ))}
          </div>
        </div>

        {/* 자주 나온 질문 */}
        <div className="ap-section">
          <div className="ap-section-title">시청자 주요 질문</div>
          <div className="ap-questions">
            {a.topQuestions.map((q, i) => (
              <div key={i} className="ap-question">
                <span className="ap-q-num">{i + 1}</span>
                <span className="ap-q-text">{q}</span>
              </div>
            ))}
          </div>
        </div>

        {/* AI 인사이트 */}
        <div className="ap-section">
          <div className="ap-section-title">AI 인사이트 & 추천</div>
          <div className="ap-insights">
            {a.insights.map((ins, i) => (
              <div key={i} className="ap-insight">
                <span className="ap-insight-dot">✦</span>
                <span className="ap-insight-text">{ins}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <style jsx>{`
        .ap { display: flex; flex-direction: column; height: 100%; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.08); border-radius: 14px; overflow: hidden; }
        .ap-header { padding: 16px 20px; border-bottom: 1px solid rgba(255,255,255,0.07); display: flex; align-items: center; gap: 12px; }
        .ap-icon { font-size: 24px; }
        .ap-title { font-size: 15px; font-weight: 700; color: #f1f5f9; }
        .ap-sub { font-size: 11px; color: #64748b; margin-top: 2px; }
        .ap-body { flex: 1; overflow-y: auto; padding: 16px; display: flex; flex-direction: column; gap: 20px; scrollbar-width: thin; scrollbar-color: rgba(255,255,255,0.1) transparent; }
        .ap-body::-webkit-scrollbar { width: 4px; }
        .ap-body::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 2px; }
        .ap-section { display: flex; flex-direction: column; gap: 10px; }
        .ap-section-title { font-size: 11px; font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; color: #6366f1; }
        .ap-metrics { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
        .ap-metric { background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.07); border-radius: 10px; padding: 12px 10px; text-align: center; }
        .ap-metric-val { font-size: 18px; font-weight: 800; color: #f1f5f9; }
        .ap-metric-label { font-size: 10px; color: #64748b; margin-top: 4px; }
        .ap-sentiment-bars { display: flex; flex-direction: column; gap: 8px; }
        .ap-sent-row { display: flex; align-items: center; gap: 8px; }
        .ap-sent-label { font-size: 11px; font-weight: 600; width: 28px; }
        .ap-sent-label.positive { color: #6ee7b7; }
        .ap-sent-label.neutral { color: #a5b4fc; }
        .ap-sent-label.negative { color: #fca5a5; }
        .ap-bar-wrap { flex: 1; height: 8px; background: rgba(255,255,255,0.06); border-radius: 999px; overflow: hidden; }
        .ap-bar { height: 100%; border-radius: 999px; transition: width 0.8s ease; }
        .ap-sent-pct { font-size: 11px; font-weight: 700; color: #94a3b8; width: 30px; text-align: right; }
        .ap-keywords { display: flex; flex-wrap: wrap; gap: 6px; }
        .ap-keyword { font-size: 12px; font-weight: 600; color: #a5b4fc; background: rgba(99,102,241,0.1); border: 1px solid rgba(99,102,241,0.2); padding: 4px 10px; border-radius: 999px; }
        .ap-questions { display: flex; flex-direction: column; gap: 6px; }
        .ap-question { display: flex; align-items: flex-start; gap: 10px; padding: 10px 12px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 8px; }
        .ap-q-num { font-size: 11px; font-weight: 700; color: #6366f1; background: rgba(99,102,241,0.15); width: 20px; height: 20px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
        .ap-q-text { font-size: 13px; color: #94a3b8; line-height: 1.55; }
        .ap-insights { display: flex; flex-direction: column; gap: 8px; }
        .ap-insight { display: flex; align-items: flex-start; gap: 8px; padding: 10px 12px; background: rgba(99,102,241,0.05); border: 1px solid rgba(99,102,241,0.15); border-radius: 8px; }
        .ap-insight-dot { color: #6366f1; font-size: 10px; margin-top: 3px; flex-shrink: 0; }
        .ap-insight-text { font-size: 12px; color: #94a3b8; line-height: 1.6; }
      `}</style>
    </div>
  );
}

// ─── 메인 페이지 ─────────────────────────────────────────────────────
const API_BASE = `${process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8090'}/api/v1`;

export default function BroadcastDetail() {
  const router = useRouter();
  const { id } = router.query;

  const [campaign, setCampaign] = useState<Campaign | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [nickname, setNickname] = useState('시청자');
  const [wsConnected, setWsConnected] = useState(false);
  const [areScriptsReady, setAreScriptsReady] = useState(false);
  const [viewerCount, setViewerCount] = useState(0);
  const [linkedProductIds, setLinkedProductIds] = useState<number[]>([]);
  const [showProductPicker, setShowProductPicker] = useState(false);

  const toggleProduct = (pid: number) => {
    setLinkedProductIds((prev) =>
      prev.includes(pid) ? prev.filter((x) => x !== pid) : [...prev, pid]
    );
  };

  const clientRef = useRef<any>(null);
  const subRef = useRef<any>(null);
  const seenKeysRef = useRef<Set<string>>(new Set());

  const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
  const WS_BASE_URL = process.env.NEXT_PUBLIC_WS_URL || API_BASE_URL;

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    setError(null);
    fetch(`${API_BASE}/campaigns/${id}`)
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then((json) => {
        const data: Campaign = json.data;
        setCampaign(data);
        setLinkedProductIds(data.campaignProducts.map((p) => p.productId));
      })
      .catch(() => setError('캠페인 정보를 불러오는 데 실패했습니다.'))
      .finally(() => setLoading(false));
  }, [id]);

  useEffect(() => {
    if (!campaign || toUiStatus(campaign.status) !== 'live') return;
    const interval = setInterval(() => {
      setViewerCount((prev) => Math.max(1, prev + Math.floor(Math.random() * 20) - 8));
    }, 3000);
    return () => clearInterval(interval);
  }, [campaign?.status]);

  const markScriptsReady = () => {
    if (typeof window === 'undefined') return;
    if ((window as any).SockJS && (window as any).StompJs) setAreScriptsReady(true);
  };

  const appendMessage = (rawBody: string) => {
    if (seenKeysRef.current.has(rawBody)) return;
    seenKeysRef.current.add(rawBody);
    try {
      const p = JSON.parse(rawBody ?? '{}') as any;
      const text = p.payload?.message ?? rawBody;
      const nick = p.actor?.sender || p.actor?.username || '시청자';
      const ts = p.sentAt ? new Date(p.sentAt) : new Date();
      setMessages((prev) => [...prev, { id: Date.now() + Math.random(), nickname: nick, text, timestamp: ts }]);
    } catch {
      setMessages((prev) => [...prev, { id: Date.now() + Math.random(), nickname: '시청자', text: rawBody, timestamp: new Date() }]);
    }
  };

  const disconnect = () => {
    try { subRef.current?.unsubscribe?.(); clientRef.current?.disconnect?.(); } finally {
      clientRef.current = null; subRef.current = null; setWsConnected(false);
    }
  };

  const connect = () => {
    if (!areScriptsReady) return;
    const SockJS = (window as any).SockJS;
    const StompJs = (window as any).StompJs;
    if (!SockJS || !StompJs) return;
    disconnect();
    const socket = new SockJS(`${WS_BASE_URL}/ws`);
    const client = StompJs.Stomp.over(socket);
    client.debug = () => {};
    client.connect({}, () => {
      clientRef.current = client;
      setWsConnected(true);
      subRef.current = client.subscribe(`/sub/room/${id}`, (msg: any) => appendMessage(msg?.body ?? ''));
    }, () => setWsConnected(false));
  };

  const handleToggleWs = () => wsConnected ? disconnect() : connect();

  const sendMessage = () => {
    if (!inputValue.trim()) return;
    const nick = nickname.trim() || '시청자';
    const text = inputValue.trim();
    if (wsConnected && clientRef.current) {
      try {
        clientRef.current.send('/send/room.action', {}, JSON.stringify({
          roomId: id, action: 'CHAT.MESSAGE',
          actor: { userId: nick, username: nick, sender: nick },
          payload: { message: text },
        }));
      } catch {
        setMessages((prev) => [...prev, { id: Date.now(), nickname: nick, text, timestamp: new Date() }]);
      }
    } else {
      setMessages((prev) => [...prev, { id: Date.now(), nickname: nick, text, timestamp: new Date() }]);
    }
    setInputValue('');
  };

  useEffect(() => () => { disconnect(); }, []);

  if (!id) return null;

  if (loading) {
    return (
      <>
        <Head><title>로딩 중...</title></Head>
        <style jsx global>{`* { margin:0;padding:0;box-sizing:border-box; } body { font-family: -apple-system,sans-serif; background:#0f172a; color:#e2e8f0; min-height:100vh; } a { text-decoration:none;color:inherit; }`}</style>
        <div style={{ maxWidth: 600, margin: '0 auto', padding: '80px 24px', textAlign: 'center' }}>
          <div style={{ fontSize: 48, marginBottom: 20 }}>⏳</div>
          <p style={{ color: '#64748b' }}>방송 정보를 불러오는 중...</p>
        </div>
      </>
    );
  }

  if (error || !campaign) {
    return (
      <>
        <Head><title>방송을 찾을 수 없음</title></Head>
        <style jsx global>{`* { margin:0;padding:0;box-sizing:border-box; } body { font-family: -apple-system,sans-serif; background:#0f172a; color:#e2e8f0; min-height:100vh; } a { text-decoration:none;color:inherit; }`}</style>
        <div style={{ maxWidth: 600, margin: '0 auto', padding: '80px 24px', textAlign: 'center' }}>
          <div style={{ fontSize: 48, marginBottom: 20 }}>📡</div>
          <h1 style={{ fontSize: 24, fontWeight: 700, color: '#f1f5f9', marginBottom: 12 }}>방송을 찾을 수 없습니다</h1>
          <p style={{ color: '#64748b', marginBottom: 32 }}>{error ?? '요청하신 방송이 존재하지 않거나 삭제되었습니다.'}</p>
          <Link href="/broadcasts" style={{ padding: '10px 24px', background: 'linear-gradient(135deg,#6366f1,#8b5cf6)', color: 'white', borderRadius: 8, fontSize: 14, fontWeight: 600 }}>
            목록으로 돌아가기
          </Link>
        </div>
      </>
    );
  }

  const uiStatus = toUiStatus(campaign.status);
  const isLive = uiStatus === 'live';
  const isScheduled = uiStatus === 'scheduled';
  const productName = campaign.campaignProducts[0]?.name ?? '상품 없음';
  const startedAtLabel = campaign.startedAt
    ? new Date(campaign.startedAt).toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
    : null;

  return (
    <>
      <Head><title>{campaign.title} — Live Platform Lab</title></Head>
      <Script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js" strategy="afterInteractive" onLoad={markScriptsReady} />
      <Script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js" strategy="afterInteractive" onLoad={markScriptsReady} />

      <style jsx global>{`* { margin:0;padding:0;box-sizing:border-box; } body { font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif; background:#0f172a; color:#e2e8f0; min-height:100vh; } a { text-decoration:none;color:inherit; }`}</style>
      <style jsx>{`
        .page { display:flex; flex-direction:column; min-height:100vh; }
        .topbar { background:rgba(15,23,42,0.95); border-bottom:1px solid rgba(255,255,255,0.06); padding:0 24px; height:56px; display:flex; align-items:center; position:sticky; top:0; z-index:50; backdrop-filter:blur(12px); }
        .nav { display:flex; align-items:center; gap:8px; font-size:13px; color:#64748b; }
        .nav a:hover { color:#a5b4fc; }
        .nav-sep { color:#334155; }
        .nav-cur { color:#94a3b8; font-weight:500; max-width:260px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }

        .content { flex:1; display:flex; max-width:1400px; margin:0 auto; width:100%; padding:24px; gap:24px; }

        .main-col { flex:1; min-width:0; display:flex; flex-direction:column; gap:20px; }

        .video-wrap { position:relative; background:#000; border-radius:14px; overflow:hidden; aspect-ratio:16/9; border:1px solid rgba(255,255,255,0.08); }
        .video-ph { width:100%; height:100%; display:flex; flex-direction:column; align-items:center; justify-content:center; gap:16px; background:linear-gradient(135deg,#0f172a 0%,#1e293b 100%); }
        .video-icon { font-size:56px; opacity:0.4; }
        .video-ph-text { font-size:15px; color:#475569; font-weight:500; }

        .badge-live { position:absolute; top:16px; left:16px; display:flex; align-items:center; gap:8px; background:rgba(0,0,0,0.7); border-radius:999px; padding:6px 14px; backdrop-filter:blur(8px); }
        .live-dot { width:8px; height:8px; border-radius:50%; background:#ef4444; animation:pulse 1.5s ease-in-out infinite; }
        @keyframes pulse { 0%,100%{opacity:1} 50%{opacity:0.4} }
        .live-txt { font-size:12px; font-weight:700; color:#fca5a5; letter-spacing:0.05em; }
        .viewer-badge { position:absolute; top:16px; right:16px; background:rgba(0,0,0,0.7); border-radius:999px; padding:6px 14px; font-size:13px; color:#cbd5e1; backdrop-filter:blur(8px); }
        .status-overlay { position:absolute; top:16px; left:16px; background:rgba(0,0,0,0.7); border-radius:999px; padding:6px 14px; font-size:12px; font-weight:700; backdrop-filter:blur(8px); }
        .status-overlay.scheduled { color:#fcd34d; }
        .status-overlay.ended { color:#94a3b8; }

        .info-card { background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:14px; padding:24px; }
        .info-title { font-size:22px; font-weight:700; color:#f1f5f9; margin-bottom:12px; }
        .info-meta { display:flex; flex-wrap:wrap; align-items:center; gap:10px; margin-bottom:14px; }
        .s-badge { font-size:12px; font-weight:700; padding:4px 12px; border-radius:999px; }
        .s-live { background:rgba(239,68,68,0.15); color:#fca5a5; border:1px solid rgba(239,68,68,0.3); }
        .s-scheduled { background:rgba(251,191,36,0.12); color:#fcd34d; border:1px solid rgba(251,191,36,0.25); }
        .s-ended { background:rgba(100,116,139,0.12); color:#94a3b8; border:1px solid rgba(100,116,139,0.2); }
        .info-desc { font-size:14px; color:#94a3b8; line-height:1.7; }
        .info-started { font-size:13px; color:#64748b; }

        .product-match-section { margin-top:20px; padding-top:18px; border-top:1px solid rgba(255,255,255,0.07); display:flex; flex-direction:column; gap:10px; }
        .product-match-header { display:flex; align-items:center; justify-content:space-between; }
        .product-match-label { font-size:11px; font-weight:700; color:#64748b; letter-spacing:0.06em; text-transform:uppercase; display:flex; align-items:center; gap:6px; }
        .product-count { background:rgba(99,102,241,0.2); color:#a5b4fc; font-size:10px; font-weight:700; padding:1px 7px; border-radius:999px; }
        .btn-change-product { font-size:12px; font-weight:600; color:#6366f1; background:none; border:none; cursor:pointer; padding:2px 0; }
        .btn-change-product:hover { color:#a5b4fc; }
        .product-match-list { display:flex; flex-direction:column; gap:6px; }
        .product-match-card { display:flex; align-items:center; justify-content:space-between; gap:12px; padding:10px 14px; background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:10px; }
        .product-match-info { display:flex; align-items:center; gap:10px; flex-wrap:wrap; flex:1; min-width:0; }
        .product-match-name { font-size:13px; font-weight:700; color:#f1f5f9; }
        .product-match-order { font-size:11px; font-weight:600; padding:2px 8px; border-radius:999px; background:rgba(99,102,241,0.12); color:#a5b4fc; border:1px solid rgba(99,102,241,0.2); }
        .btn-remove-product { background:none; border:none; color:#475569; font-size:13px; cursor:pointer; padding:2px 4px; flex-shrink:0; line-height:1; }
        .btn-remove-product:hover { color:#f87171; }
        .product-match-empty { font-size:13px; color:#475569; padding:6px 0; }
        .product-picker { display:flex; flex-direction:column; gap:4px; padding:8px; background:rgba(0,0,0,0.25); border:1px solid rgba(255,255,255,0.08); border-radius:10px; }
        .picker-item { display:flex; align-items:center; justify-content:space-between; padding:9px 12px; border-radius:8px; cursor:pointer; transition:background 0.15s; }
        .picker-item:hover { background:rgba(99,102,241,0.08); }
        .picker-item.selected { background:rgba(99,102,241,0.1); }
        .picker-item-left { display:flex; align-items:center; gap:8px; }
        .picker-item-name { font-size:13px; font-weight:600; color:#e2e8f0; }
        .picker-item-order { font-size:11px; color:#64748b; }
        .picker-check { font-size:12px; font-weight:700; color:#475569; width:16px; text-align:center; }
        .picker-check.on { color:#6366f1; }

        .side-col { width:360px; flex-shrink:0; height:calc(100vh - 104px); position:sticky; top:80px; }

        @media (max-width:900px) {
          .content { flex-direction:column; padding:16px; }
          .side-col { width:100%; height:520px; position:static; }
        }
      `}</style>

      <div className="page">
        <div className="topbar">
          <nav className="nav">
            <Link href="/">홈</Link>
            <span className="nav-sep">/</span>
            <Link href="/broadcasts">방송 목록</Link>
            <span className="nav-sep">/</span>
            <span className="nav-cur">{campaign.title}</span>
          </nav>
        </div>

        <div className="content">
          <div className="main-col">
            {/* 영상 영역 */}
            <div className="video-wrap">
              <div className="video-ph">
                <div className="video-icon">{isLive ? '📡' : isScheduled ? '🕐' : '📼'}</div>
                <div className="video-ph-text">
                  {isLive ? '스트리밍 연결 중...' : isScheduled ? '방송 시작 전입니다' : '방송이 종료되었습니다'}
                </div>
              </div>
              {isLive && (
                <>
                  <div className="badge-live">
                    <div className="live-dot" />
                    <span className="live-txt">LIVE</span>
                  </div>
                  <div className="viewer-badge">👥 {viewerCount.toLocaleString()}명 시청 중</div>
                </>
              )}
              {isScheduled && <div className="status-overlay scheduled">🕐 방송 예정</div>}
              {uiStatus === 'ended' && (
                <div className="status-overlay ended">
                  종료{startedAtLabel && ` · ${startedAtLabel} 시작`}
                </div>
              )}
            </div>

            {/* 방송 정보 */}
            <div className="info-card">
              <h1 className="info-title">{campaign.title}</h1>
              <div className="info-meta">
                <span className={`s-badge s-${uiStatus}`}>
                  {isLive && '● '}{statusLabel[uiStatus]}
                </span>
                {startedAtLabel && <span className="info-started">시작 {startedAtLabel}</span>}
              </div>
              {campaign.description && <p className="info-desc">{campaign.description}</p>}

              {/* 상품 매칭 */}
              <div className="product-match-section">
                <div className="product-match-header">
                  <span className="product-match-label">
                    연결된 상품 {linkedProductIds.length > 0 && <span className="product-count">{linkedProductIds.length}</span>}
                  </span>
                  {uiStatus !== 'ended' && (
                    <button className="btn-change-product" onClick={() => setShowProductPicker((v) => !v)}>
                      {showProductPicker ? '닫기' : '+ 상품 추가'}
                    </button>
                  )}
                </div>

                {linkedProductIds.length === 0 ? (
                  <div className="product-match-empty">연결된 상품이 없습니다.</div>
                ) : (
                  <div className="product-match-list">
                    {linkedProductIds.map((pid) => {
                      const p = campaign.campaignProducts.find((x) => x.productId === pid);
                      if (!p) return null;
                      return (
                        <div key={pid} className="product-match-card">
                          <div className="product-match-info">
                            <span className="product-match-name">{p.name}</span>
                            <span className="product-match-order">#{p.displayOrder}</span>
                          </div>
                          {uiStatus !== 'ended' && (
                            <button className="btn-remove-product" onClick={() => toggleProduct(pid)}>✕</button>
                          )}
                        </div>
                      );
                    })}
                  </div>
                )}

                {uiStatus !== 'ended' && showProductPicker && (
                  <div className="product-picker">
                    {campaign.campaignProducts.map((p) => {
                      const isLinked = linkedProductIds.includes(p.productId);
                      return (
                        <div
                          key={p.productId}
                          className={`picker-item ${isLinked ? 'selected' : ''}`}
                          onClick={() => toggleProduct(p.productId)}
                        >
                          <div className="picker-item-left">
                            <span className="picker-item-name">{p.name}</span>
                            <span className="picker-item-order">순서 {p.displayOrder}</span>
                          </div>
                          <div className="picker-item-right">
                            <span className={`picker-check ${isLinked ? 'on' : ''}`}>{isLinked ? '✓' : '+'}</span>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* 우측 패널: 상태별 */}
          <div className="side-col">
            {(isScheduled || isLive) && (
              <ChatQnAPanel
                status={uiStatus}
                productName={productName}
                messages={messages}
                inputValue={inputValue}
                setInputValue={setInputValue}
                nickname={nickname}
                setNickname={setNickname}
                onSend={sendMessage}
                wsConnected={wsConnected}
                onToggleWs={handleToggleWs}
                areScriptsReady={areScriptsReady}
              />
            )}
            {uiStatus === 'ended' && <EndedAnalysisPanel />}
          </div>
        </div>
      </div>
    </>
  );
}
