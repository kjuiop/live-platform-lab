import React, { useState, useRef, useEffect, useMemo, useCallback } from 'react';
import Head from 'next/head';
import Link from 'next/link';
import Script from 'next/script';
import { useRouter } from 'next/router';

type BroadcastStatus = 'scheduled' | 'live' | 'ended';

interface BroadcastStatusData {
  status: string;
  startedAt: string | null;
  endedAt: string | null;
  chatRoomId: string | null;
}

interface CampaignProduct {
  productId: number;
  name: string;
  displayOrder: number;
}

interface ProductFaqSample {
  id: number;
  question: string;
  answer: string;
}

interface ProductFaqGroup {
  productId: number;
  productName: string;
  items: ProductFaqSample[];
}

interface CampaignReport {
  id: number;
  campaignId: number;
  roomId: string;
  totalViewers: number;
  peakConcurrent: number;
  avgDurationSeconds: number;
  totalMessages: number;
  totalQuestions: number;
  aiAnswerCount: number;
  aiReportText: string | null;
  startedAt: string | null;
  endedAt: string | null;
  unansweredQuestions: string[];
}

interface Campaign {
  id: number;
  title: string;
  description: string;
  status: string;
  scheduledAt: string | null;
  startedAt: string | null;
  endedAt: string | null;
  chatRoomId: string | null;
  campaignProducts: CampaignProduct[];
}

function toUiStatus(apiStatus: string): BroadcastStatus {
  if (apiStatus === 'ON_AIR') return 'live';
  if (apiStatus === 'ENDED') return 'ended';
  return 'scheduled';
}

function formatDuration(seconds: number): string {
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return m > 0 ? `${m}분 ${s}초` : `${s}초`;
}

interface Message {
  id: number;
  nickname: string;
  text: string;
  timestamp: Date;
  msgType?: 'chat' | 'faq-question' | 'faq-answer' | 'faq-error';
  faqQuestion?: string;
}




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
  onSendFaq,
  defaultProductId,
  chatStats,
  aiReport,
  aiReportLoading,
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
  onSendFaq?: (question: string, productId: number) => void;
  defaultProductId?: number;
  chatStats?: {
    totalViewers: number;
    peakConcurrent: number;
    avgDurationSeconds: number;
    totalMessages: number;
    faqCount: number;
  } | null;
  aiReport?: CampaignReport | null;
  aiReportLoading?: boolean;
}) {
  const isScheduled = status === 'scheduled';
  const isLive = status === 'live';
  const isEnded = status === 'ended';
  const [tab, setTab] = useState<'report' | 'chat' | 'faq'>(isEnded ? 'report' : 'chat');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const faqBottomRef = useRef<HTMLDivElement>(null);
  const chatContainerRef = useRef<HTMLDivElement>(null);
  const isAtChatBottomRef = useRef(true);
  const [faqInput, setFaqInput] = useState('');

  const chatMessages = useMemo(
    () => messages.filter((m) => !m.msgType || m.msgType === 'chat'),
    [messages]
  );
  const faqMessages = useMemo(
    () => messages.filter((m) => m.msgType === 'faq-question' || m.msgType === 'faq-answer' || m.msgType === 'faq-error'),
    [messages]
  );
  const faqQuestionCount = useMemo(
    () => faqMessages.filter((m) => m.msgType === 'faq-question').length,
    [faqMessages]
  );


  useEffect(() => {
    if (tab === 'chat' && isAtChatBottomRef.current) {
      const el = chatContainerRef.current;
      if (el) el.scrollTop = el.scrollHeight;
    }
  }, [messages, tab]);

  useEffect(() => {
    if (tab === 'faq') faqBottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [faqMessages, tab]);

  const handleChatScroll = useCallback(() => {
    const el = chatContainerRef.current;
    if (!el) return;
    isAtChatBottomRef.current = el.scrollHeight - el.scrollTop - el.clientHeight < 80;
  }, []);

  const handleKey = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') onSend();
  };

  return (
    <div className="lp">
      <div className="lp-header">
        <div className="lp-tabs">
          {isEnded && (
            <button className={`lp-tab ${tab === 'report' ? 'active' : ''}`} onClick={() => setTab('report')}>
              📊 AI 리포트
            </button>
          )}
          <button className={`lp-tab ${tab === 'chat' ? 'active' : ''}`} onClick={() => setTab('chat')}>
            💬 채팅
            {isEnded && <span className="tab-stat-badge">{(chatStats?.totalMessages ?? chatMessages.length).toLocaleString()}</span>}
          </button>
          <button className={`lp-tab ${tab === 'faq' ? 'active' : ''}`} onClick={() => setTab('faq')}>
            🤖 FAQ
            {isLive && faqMessages.length > 0 && <span className="faq-count">{faqQuestionCount}</span>}
            {isEnded && <span className="tab-stat-badge">{chatStats?.faqCount ?? faqQuestionCount}</span>}
          </button>
        </div>
      </div>

      {tab === 'report' && isEnded ? (
        <div className="lp-report-body">
          <div className="rp-section">
            <div className="rp-section-title">주요 지표</div>
            <div className="rp-metrics">
              <div className="rp-metric">
                <div className="rp-metric-val">{(aiReport?.totalViewers ?? chatStats?.totalViewers ?? 0).toLocaleString()}</div>
                <div className="rp-metric-label">누적 시청자</div>
              </div>
              <div className="rp-metric">
                <div className="rp-metric-val">{(aiReport?.peakConcurrent ?? chatStats?.peakConcurrent ?? 0).toLocaleString()}</div>
                <div className="rp-metric-label">최고 동시 시청자</div>
              </div>
              <div className="rp-metric">
                <div className="rp-metric-val">{formatDuration(aiReport?.avgDurationSeconds ?? chatStats?.avgDurationSeconds ?? 0)}</div>
                <div className="rp-metric-label">평균 시청 시간</div>
              </div>
              <div className="rp-metric">
                <div className="rp-metric-val">{(aiReport?.totalMessages ?? chatStats?.totalMessages ?? 0).toLocaleString()}</div>
                <div className="rp-metric-label">총 채팅 수</div>
              </div>
              <div className="rp-metric">
                <div className="rp-metric-val">{(aiReport?.totalQuestions ?? chatStats?.faqCount ?? 0).toLocaleString()}</div>
                <div className="rp-metric-label">FAQ 질문 수</div>
              </div>
              <div className="rp-metric">
                <div className="rp-metric-val">{(aiReport?.aiAnswerCount ?? 0).toLocaleString()}</div>
                <div className="rp-metric-label">AI 답변 수</div>
              </div>
            </div>
          </div>
          {aiReport?.unansweredQuestions?.length > 0 && (
            <div className="rp-section">
              <div className="rp-section-title">AI 미답변 질문</div>
              <div className="rp-questions">
                {aiReport.unansweredQuestions.map((q, i) => (
                  <div key={i} className="rp-question">
                    <span className="rp-q-num">{i + 1}</span>
                    <span className="rp-q-text">{q}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
          <div className="rp-section">
            <div className="rp-section-title">AI 인사이트 & 추천</div>
            {aiReportLoading ? (
              <div className="rp-ai-loading">
                <div className="rp-ai-spinner" />
                <span>AI가 방송을 분석하고 있습니다<span className="dots" /></span>
              </div>
            ) : aiReport?.aiReportText ? (
              <div className="rp-ai-text">
                {aiReport.aiReportText.split('\n').filter(Boolean).map((line, i) => (
                  <div key={i} className="rp-insight">
                    <span className="rp-insight-dot">✦</span>
                    <span className="rp-insight-text">{line}</span>
                  </div>
                ))}
              </div>
            ) : (
              <div className="rp-ai-unavailable">AI 리포트를 아직 불러올 수 없습니다.</div>
            )}
          </div>
        </div>
      ) : tab === 'chat' ? (
        isScheduled ? (
          /* 예정: 채팅 비활성 안내 */
          <div className="lp-messages">
            <div className="lp-faq-unavailable">
              <div className="lp-faq-unavailable-icon">💬</div>
              <div className="lp-faq-unavailable-text">방송 시작 후 이용할 수 있습니다</div>
              <div className="lp-faq-unavailable-sub">방송이 시작되면 채팅에 참여할 수 있어요</div>
            </div>
          </div>
        ) : (
          <>
            <div className="lp-messages" ref={chatContainerRef} onScroll={handleChatScroll}>
              {chatMessages.length === 0 ? (
                <div className="lp-empty">채팅을 시작해보세요!</div>
              ) : (
                chatMessages.map((msg) => (
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
            {status === 'ended' ? (
              <div className="lp-ended-notice">방송이 종료되었습니다.</div>
            ) : (
              <>
                <div className="lp-nick-row">
                  <span className="lp-nick-label">닉네임</span>
                  <input className="lp-nick-input" value={nickname} onChange={(e) => setNickname(e.target.value)} maxLength={20} disabled={isLive && !wsConnected} />
                </div>
                <div className="lp-input-row">
                  <input
                    className="lp-input"
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    onKeyPress={handleKey}
                    placeholder={isLive && !wsConnected ? '채팅 연결 중...' : '메시지를 입력하세요...'}
                    disabled={isLive && !wsConnected}
                  />
                  <button className="lp-send" onClick={onSend} disabled={(isLive && !wsConnected) || !inputValue.trim()}>전송</button>
                </div>
              </>
            )}
          </>
        )
      ) : isScheduled ? (
        /* 예정: FAQ 비활성 안내 */
        <div className="lp-faq-body">
          <div className="lp-faq-unavailable">
            <div className="lp-faq-unavailable-icon">🤖</div>
            <div className="lp-faq-unavailable-text">방송 시작 후 이용할 수 있습니다</div>
            <div className="lp-faq-unavailable-sub">방송 중 AI에게 상품 관련 질문을 해보세요</div>
          </div>
        </div>
      ) : (
        /* 라이브: 실시간 AI FAQ 탭 */
        <>
          <div className="lp-faq-body">
            {faqMessages.length === 0 ? (
              <div className="lp-faq-info">AI에게 상품 관련 질문을 해보세요. 답변이 채팅방 전체에 공유됩니다.</div>
            ) : (
              faqMessages.map((msg) => {
                if (msg.msgType === 'faq-question') {
                  return (
                    <div key={msg.id} className="qna-item">
                      <div className="qna-row">
                        <span className="qna-badge q">Q</span>
                        <div style={{ flex: 1 }}>
                          <span className="qna-text">{msg.text}</span>
                          <div style={{ fontSize: 10, color: '#475569', marginTop: 3 }}>
                            {msg.nickname} · {msg.timestamp.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })}
                          </div>
                        </div>
                      </div>
                    </div>
                  );
                }
                if (msg.msgType === 'faq-answer') {
                  return (
                    <div key={msg.id} className="qna-item faq-answer-item">
                      {msg.faqQuestion && (
                        <div className="qna-row" style={{ opacity: 0.6 }}>
                          <span className="qna-badge q" style={{ fontSize: 9 }}>Q</span>
                          <span className="qna-text" style={{ fontSize: 12 }}>{msg.faqQuestion}</span>
                        </div>
                      )}
                      <div className="qna-row">
                        <span className="qna-badge a">AI</span>
                        <span className="qna-text answer">{msg.text}</span>
                      </div>
                    </div>
                  );
                }
                if (msg.msgType === 'faq-error') {
                  return (
                    <div key={msg.id} className="qna-item faq-error-item">
                      <div className="qna-row">
                        <span className="qna-badge err">!</span>
                        <span className="qna-text" style={{ color: '#fca5a5' }}>{msg.text}</span>
                      </div>
                    </div>
                  );
                }
                return null;
              })
            )}
            <div ref={faqBottomRef} />
          </div>
          {isLive && wsConnected && onSendFaq && defaultProductId != null && (
            <div className="lp-input-row">
              <input
                className="lp-input"
                value={faqInput}
                onChange={(e) => setFaqInput(e.target.value)}
                onKeyPress={(e) => {
                  if (e.key === 'Enter' && faqInput.trim()) {
                    onSendFaq(faqInput.trim(), defaultProductId);
                    setFaqInput('');
                  }
                }}
                placeholder="AI에게 상품 질문하기..."
              />
              <button
                className="lp-send ai"
                onClick={() => {
                  if (faqInput.trim()) {
                    onSendFaq(faqInput.trim(), defaultProductId);
                    setFaqInput('');
                  }
                }}
                disabled={!faqInput.trim()}
              >
                질문
              </button>
            </div>
          )}
        </>
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
.lp-ended-notice { padding: 14px 16px; font-size: 12px; color: #64748b; text-align: center; border-top: 1px solid rgba(255,255,255,0.07); }
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
        .lp-faq-unavailable { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 10px; padding: 40px 20px; }
        .lp-faq-unavailable-icon { font-size: 36px; opacity: 0.3; }
        .lp-faq-unavailable-text { font-size: 14px; font-weight: 600; color: #475569; }
        .lp-faq-unavailable-sub { font-size: 12px; color: #334155; text-align: center; line-height: 1.6; }
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
        .qna-badge.err { background: rgba(239,68,68,0.15); color: #fca5a5; border: 1px solid rgba(239,68,68,0.25); }
        .faq-answer-item { background: rgba(16,185,129,0.04); border-color: rgba(16,185,129,0.15); }
        .faq-error-item { background: rgba(239,68,68,0.04); border-color: rgba(239,68,68,0.15); }
        .qna-text { font-size: 13px; color: #cbd5e1; line-height: 1.6; }
        .qna-text.answer { color: #94a3b8; }
        .qna-loading { font-size: 13px; color: #6ee7b7; display: flex; align-items: center; gap: 4px; }
        .dots::after { content: '...'; animation: dotanim 1.2s steps(4, end) infinite; }
        @keyframes dotanim { 0%,100% { content: ''; } 25% { content: '.'; } 50% { content: '..'; } 75% { content: '...'; } }
        .lp-report-body { flex: 1; overflow-y: auto; padding: 16px; display: flex; flex-direction: column; gap: 18px; scrollbar-width: thin; scrollbar-color: rgba(255,255,255,0.1) transparent; }
        .lp-report-body::-webkit-scrollbar { width: 4px; }
        .lp-report-body::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 2px; }
        .rp-section { display: flex; flex-direction: column; gap: 10px; }
        .rp-section-title { font-size: 11px; font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; color: #6366f1; }
        .rp-metrics { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
        .tab-stat-badge { background: rgba(99,102,241,0.2); color: #a5b4fc; font-size: 10px; font-weight: 700; padding: 1px 6px; border-radius: 999px; margin-left: 4px; }
        .rp-metric { background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.07); border-radius: 10px; padding: 12px 10px; text-align: center; }
        .rp-metric-val { font-size: 16px; font-weight: 800; color: #f1f5f9; }
        .rp-metric-label { font-size: 10px; color: #64748b; margin-top: 4px; }
        .rp-sent-bars { display: flex; flex-direction: column; gap: 8px; }
        .rp-sent-row { display: flex; align-items: center; gap: 8px; }
        .rp-sent-label { font-size: 11px; font-weight: 600; width: 28px; }
        .rp-sent-label.positive { color: #6ee7b7; }
        .rp-sent-label.neutral { color: #a5b4fc; }
        .rp-sent-label.negative { color: #fca5a5; }
        .rp-bar-wrap { flex: 1; height: 8px; background: rgba(255,255,255,0.06); border-radius: 999px; overflow: hidden; }
        .rp-bar { height: 100%; border-radius: 999px; }
        .rp-sent-pct { font-size: 11px; font-weight: 700; color: #94a3b8; width: 30px; text-align: right; }
        .rp-keywords { display: flex; flex-wrap: wrap; gap: 6px; }
        .rp-keyword { font-size: 12px; font-weight: 600; color: #a5b4fc; background: rgba(99,102,241,0.1); border: 1px solid rgba(99,102,241,0.2); padding: 4px 10px; border-radius: 999px; }
        .rp-questions { display: flex; flex-direction: column; gap: 6px; }
        .rp-question { display: flex; align-items: flex-start; gap: 10px; padding: 10px 12px; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06); border-radius: 8px; }
        .rp-q-num { font-size: 11px; font-weight: 700; color: #6366f1; background: rgba(99,102,241,0.15); width: 20px; height: 20px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
        .rp-q-text { font-size: 13px; color: #94a3b8; line-height: 1.55; }
        .rp-insights { display: flex; flex-direction: column; gap: 8px; }
        .rp-insight { display: flex; align-items: flex-start; gap: 8px; padding: 10px 12px; background: rgba(99,102,241,0.05); border: 1px solid rgba(99,102,241,0.15); border-radius: 8px; }
        .rp-insight-dot { color: #6366f1; font-size: 10px; margin-top: 3px; flex-shrink: 0; }
        .rp-insight-text { font-size: 12px; color: #94a3b8; line-height: 1.6; }
        .rp-ai-loading { display: flex; align-items: center; gap: 10px; padding: 16px 12px; background: rgba(99,102,241,0.05); border: 1px solid rgba(99,102,241,0.15); border-radius: 8px; font-size: 13px; color: #a5b4fc; }
        .rp-ai-spinner { width: 14px; height: 14px; border: 2px solid rgba(99,102,241,0.3); border-top-color: #6366f1; border-radius: 50%; animation: spin 0.8s linear infinite; flex-shrink: 0; }
        @keyframes spin { to { transform: rotate(360deg); } }
        .rp-ai-text { display: flex; flex-direction: column; gap: 8px; }
        .rp-ai-unavailable { font-size: 13px; color: #475569; padding: 12px; text-align: center; }
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
          <div className="ap-section-title">AI 미답변 질문</div>
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
const CHAT_API_BASE = `${process.env.NEXT_PUBLIC_CHAT_URL ?? 'http://localhost:8080'}/api/v1`;

export default function BroadcastDetail() {
  const router = useRouter();
  const { id } = router.query;

  const [campaign, setCampaign] = useState<Campaign | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [simRunning, setSimRunning] = useState(false);

  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [nickname, setNickname] = useState('시청자');
  const [wsConnected, setWsConnected] = useState(false);
  const [areScriptsReady, setAreScriptsReady] = useState(false);
  const [viewerCount, setViewerCount] = useState(0);
  const [chatStats, setChatStats] = useState<{
    totalViewers: number;
    peakConcurrent: number;
    avgDurationSeconds: number;
    totalMessages: number;
    faqCount: number;
  } | null>(null);
  const [aiReport, setAiReport] = useState<CampaignReport | null>(null);
  const [aiReportLoading, setAiReportLoading] = useState(false);
  const [productFaqGroups, setProductFaqGroups] = useState<ProductFaqGroup[]>([]);
  const aiPollRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const [linkedProductIds, setLinkedProductIds] = useState<number[]>([]);
  const [showProductPicker, setShowProductPicker] = useState(false);
  const [elapsedSeconds, setElapsedSeconds] = useState(0);
  const [actionLoading, setActionLoading] = useState(false);
  const [activeBannerProductId, setActiveBannerProductId] = useState<number | null>(null);
  const timerIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const toggleProduct = (pid: number) => {
    setLinkedProductIds((prev) =>
      prev.includes(pid) ? prev.filter((x) => x !== pid) : [...prev, pid]
    );
  };

  const clientRef = useRef<any>(null);
  const subRef = useRef<any>(null);
  const seenKeysRef = useRef<Set<string>>(new Set());
  const isConnectingRef = useRef(false);
  const historyLoadedRoomsRef = useRef<Set<string>>(new Set());

  const WS_BASE_URL = process.env.NEXT_PUBLIC_WS_URL ?? 'http://localhost:8080';

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
    if (!campaign || campaign.campaignProducts.length === 0) return;
    Promise.all(
      campaign.campaignProducts.map(async (p) => {
        try {
          const res = await fetch(`${API_BASE}/products/${p.productId}/faq-samples`);
          const json = await res.json();
          return { productId: p.productId, productName: p.name, items: json?.data?.items ?? [] } as ProductFaqGroup;
        } catch {
          return { productId: p.productId, productName: p.name, items: [] } as ProductFaqGroup;
        }
      })
    ).then((groups) => setProductFaqGroups(groups.filter((g) => g.items.length > 0)));
  }, [campaign]);

  useEffect(() => {
    if (timerIntervalRef.current) {
      clearInterval(timerIntervalRef.current);
      timerIntervalRef.current = null;
    }
    if (!campaign || toUiStatus(campaign.status) !== 'live') return;
    const base = campaign.startedAt ? new Date(campaign.startedAt).getTime() : Date.now();
    setElapsedSeconds(Math.floor((Date.now() - base) / 1000));
    timerIntervalRef.current = setInterval(() => {
      setElapsedSeconds(Math.floor((Date.now() - base) / 1000));
    }, 1000);
    return () => {
      if (timerIntervalRef.current) clearInterval(timerIntervalRef.current);
    };
  }, [campaign?.status, campaign?.startedAt]);

  const formatElapsed = (secs: number) => {
    const h = Math.floor(secs / 3600);
    const m = Math.floor((secs % 3600) / 60);
    const s = secs % 60;
    return h > 0
      ? `${h}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
      : `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
  };

  const handleStart = async () => {
    setActionLoading(true);
    try {
      const res = await fetch(`${API_BASE}/campaigns/${id}/start`, { method: 'POST' });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const json = await res.json();
      const data: BroadcastStatusData = json.data;
      setCampaign((prev) => prev ? { ...prev, status: data.status, startedAt: data.startedAt, chatRoomId: data.chatRoomId ?? prev.chatRoomId } : prev);
      if (data.chatRoomId) connect(data.chatRoomId);
    } catch {
      alert('방송 시작에 실패했습니다.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleEnd = async () => {
    if (!confirm('방송을 종료하시겠습니까?')) return;
    setActionLoading(true);
    try {
      const res = await fetch(`${API_BASE}/campaigns/${id}/end`, { method: 'POST' });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const json = await res.json();
      const data: BroadcastStatusData = json.data;
      setCampaign((prev) => prev ? { ...prev, status: data.status, endedAt: data.endedAt, chatRoomId: data.chatRoomId ?? null } : prev);
      if (!data.chatRoomId) disconnect();
    } catch {
      alert('방송 종료에 실패했습니다.');
    } finally {
      setActionLoading(false);
    }
  };

  const markScriptsReady = () => {
    if (typeof window === 'undefined') return;
    if ((window as any).SockJS && (window as any).StompJs) setAreScriptsReady(true);
  };

  const SEEN_MAX = 200;

  const appendMessage = (rawBody: string) => {
    try {
      const p = JSON.parse(rawBody ?? '{}') as any;
      if (p.action === 'VIEWER.COUNT') {
        setViewerCount(p.payload?.count ?? 0);
        return;
      }
      if (p.action === 'PRODUCT.BANNER.ON') {
        setActiveBannerProductId(p.productId ?? null);
        return;
      }
      if (p.action === 'PRODUCT.BANNER.OFF') {
        setActiveBannerProductId(null);
        return;
      }
      if (p.action === 'CHAT.JOIN') {
        if (p.activeBanner != null) {
          setActiveBannerProductId(p.activeBanner.productId ?? null);
        }
        return;
      }
    } catch {
      // fall through to normal handling
    }
    if (seenKeysRef.current.has(rawBody)) return;
    seenKeysRef.current.add(rawBody);
    if (seenKeysRef.current.size > SEEN_MAX) {
      seenKeysRef.current.delete(seenKeysRef.current.values().next().value!);
    }
    try {
      const p = JSON.parse(rawBody ?? '{}') as any;
      const nick = p.actor?.sender || p.actor?.username || '시청자';
      const ts = p.sentAt ? new Date(p.sentAt) : new Date();
      if (p.action === 'CHAT.MESSAGE') {
        const text = p.payload?.message ?? rawBody;
        setMessages((prev) => [...prev, { id: Date.now() + Math.random(), nickname: nick, text, timestamp: ts, msgType: 'chat' }]);
      } else if (p.action === 'FAQ.QUESTION') {
        const text = p.payload?.question ?? '';
        setMessages((prev) => [...prev, { id: Date.now() + Math.random(), nickname: nick, text, timestamp: ts, msgType: 'faq-question' }]);
      } else if (p.action === 'FAQ.ANSWER') {
        const text = p.payload?.answer ?? '';
        const faqQuestion = p.payload?.question;
        setMessages((prev) => [...prev, { id: Date.now() + Math.random(), nickname: nick, text, timestamp: ts, msgType: 'faq-answer', faqQuestion }]);
      } else if (p.action === 'FAQ.ERROR') {
        const text = p.payload?.message ?? '답변 생성에 실패했습니다.';
        setMessages((prev) => [...prev, { id: Date.now() + Math.random(), nickname: nick, text, timestamp: ts, msgType: 'faq-error' }]);
      }
    } catch {
      setMessages((prev) => [...prev, { id: Date.now() + Math.random(), nickname: '시청자', text: rawBody, timestamp: new Date(), msgType: 'chat' }]);
    }
  };

  const publishJoin = (client: any, roomId: string) => {
    const nick = nickname.trim() || '시청자';
    try {
      client.send('/send/room.action', {}, JSON.stringify({
        roomId,
        action: 'CHAT.JOIN',
        actor: { userId: nick, username: nick, sender: nick },
        payload: {},
      }));
    } catch {
      // join 실패는 무시
    }
  };

  const publishLeave = () => {
    if (!wsConnected || !clientRef.current || !campaign?.chatRoomId) return;
    const nick = nickname.trim() || '시청자';
    try {
      clientRef.current.send('/send/room.action', {}, JSON.stringify({
        roomId: campaign.chatRoomId,
        action: 'CHAT.LEAVE',
        actor: { userId: nick, username: nick, sender: nick },
        payload: {},
      }));
    } catch {
      // leave 실패는 무시
    }
  };

  const publishBannerOn = (productId: number, productName: string) => {
    if (!clientRef.current || !campaign?.chatRoomId) return;
    const nick = nickname.trim() || '시청자';
    try {
      clientRef.current.send('/send/room.action', {}, JSON.stringify({
        roomId: campaign.chatRoomId,
        action: 'PRODUCT.BANNER.ON',
        actor: { userId: nick, username: nick, sender: nick },
        payload: { productId, productName },
      }));
    } catch { /* 무시 */ }
  };

  const publishBannerOff = () => {
    if (!clientRef.current || !campaign?.chatRoomId) return;
    const nick = nickname.trim() || '시청자';
    try {
      clientRef.current.send('/send/room.action', {}, JSON.stringify({
        roomId: campaign.chatRoomId,
        action: 'PRODUCT.BANNER.OFF',
        actor: { userId: nick, username: nick, sender: nick },
        payload: {},
      }));
    } catch { /* 무시 */ }
  };

  const SIM_VIEWER_NAMES = ['하나', '두리', '세리', '네모', '다솜', '여섯', '일곱', '여덟'];

  const runSimulation = async () => {
    if (!campaign?.chatRoomId) return;
    const roomId = campaign.chatRoomId;
    const productId = campaign.campaignProducts?.[0]?.productId ?? 1;
    const SockJS = (window as any).SockJS;
    const StompJs = (window as any).StompJs;
    if (!SockJS || !StompJs) return;

    setSimRunning(true);
    try {
      const res = await fetch(`${API_BASE}/products/${productId}/simulation-messages`);
      const json = await res.json();
      const chatMessages: string[] = json?.data?.chatMessages ?? [];
      const faqQuestions: string[] = json?.data?.faqQuestions ?? [];
      if (chatMessages.length === 0) {
        alert('시뮬레이션 메시지가 없습니다. 상품 상세에서 먼저 생성해주세요.');
        return;
      }

      const sleep = (ms: number) => new Promise<void>((r) => setTimeout(r, ms));
      const rand = (min: number, max: number) => Math.floor(Math.random() * (max - min)) + min;
      const sendAction = (client: any, action: string, actor: string, payload: object) => {
        try {
          client.send('/send/room.action', {}, JSON.stringify({
            roomId, action,
            actor: { userId: actor, username: actor, sender: actor },
            payload,
          }));
        } catch { /* 무시 */ }
      };

      // Phase 1: 가상 시청자 순차 입장
      const viewers: { name: string; client: any }[] = [];
      for (const name of SIM_VIEWER_NAMES) {
        await new Promise<void>((resolve) => {
          const socket = new SockJS(`${WS_BASE_URL}/ws`);
          const client = StompJs.Stomp.over(socket);
          client.debug = () => {};
          client.heartbeat.outgoing = 0;
          client.heartbeat.incoming = 0;
          client.connect({}, () => {
            client.subscribe(`/sub/room/${roomId}`, () => {});
            sendAction(client, 'CHAT.JOIN', name, {});
            viewers.push({ name, client });
            resolve();
          }, () => resolve());
        });
        await sleep(rand(500, 1500));
      }

      // Phase 2: 채팅 + FAQ 혼합 (4채팅마다 FAQ 1회)
      // 목표 총 시뮬레이션 시간 10분 기준으로 메시지당 딜레이 자동 계산
      const TARGET_DURATION_MS = 10 * 60 * 1000;
      const viewerOverheadMs = viewers.length * 1000 * 2;
      const chatBudgetMs = Math.max(TARGET_DURATION_MS - viewerOverheadMs, 1000);
      const baseIntervalMs = Math.max(Math.floor(chatBudgetMs / chatMessages.length), 500);
      const minChatMs = Math.floor(baseIntervalMs * 0.7);
      const maxChatMs = Math.floor(baseIntervalMs * 1.3);

      let faqIdx = 0;
      const messageCount = chatMessages.length;
      for (let i = 0; i < messageCount; i++) {
        const viewer = viewers[i % viewers.length];
        if (i > 0 && i % 4 === 0 && faqIdx < faqQuestions.length) {
          sendAction(viewer.client, 'FAQ.QUESTION', viewer.name, {
            question: faqQuestions[faqIdx++], productId,
          });
          await sleep(rand(minChatMs, maxChatMs));
        }
        sendAction(viewer.client, 'CHAT.MESSAGE', viewer.name, { message: chatMessages[i % chatMessages.length] });
        await sleep(rand(minChatMs, maxChatMs));
      }

      // 남은 FAQ 발송
      while (faqIdx < faqQuestions.length) {
        const viewer = viewers[faqIdx % viewers.length];
        sendAction(viewer.client, 'FAQ.QUESTION', viewer.name, {
          question: faqQuestions[faqIdx++], productId,
        });
        await sleep(rand(minChatMs, maxChatMs));
      }

      // Phase 3: 가상 시청자 순차 퇴장 (랜덤)
      for (const viewer of viewers) {
        sendAction(viewer.client, 'CHAT.LEAVE', viewer.name, {});
        await sleep(rand(500, 1500));
        try { viewer.client.disconnect(); } catch { /* 무시 */ }
      }
    } finally {
      setSimRunning(false);
    }
  };

  const disconnect = () => {
    isConnectingRef.current = false;
    if (campaign?.chatRoomId) historyLoadedRoomsRef.current.delete(campaign.chatRoomId);
    publishLeave();
    try { subRef.current?.unsubscribe?.(); clientRef.current?.disconnect?.(); } finally {
      clientRef.current = null; subRef.current = null; setWsConnected(false);
    }
  };

  const loadHistory = async (roomId: string) => {
    if (historyLoadedRoomsRef.current.has(roomId)) return;
    try {
      const res = await fetch(`${CHAT_API_BASE}/rooms/${roomId}/messages?limit=100`);
      if (!res.ok) return;
      const json = await res.json();
      const items: any[] = json.data ?? [];
      const history: Message[] = items.map((item) => {
        const action: string = item.action ?? '';
        const msgType: Message['msgType'] =
          action === 'CHAT.MESSAGE' ? 'chat' :
          action === 'FAQ.QUESTION' ? 'faq-question' :
          action === 'FAQ.ANSWER' ? 'faq-answer' :
          action === 'FAQ.ERROR' ? 'faq-error' : 'chat';
        const text =
          action === 'CHAT.MESSAGE' ? (item.payload?.message ?? '') :
          action === 'FAQ.QUESTION' ? (item.payload?.question ?? '') :
          action === 'FAQ.ANSWER' ? (item.payload?.answer ?? '') :
          action === 'FAQ.ERROR' ? (item.payload?.message ?? '') : '';
        return {
          id: item.id ?? Date.now() + Math.random(),
          nickname: item.senderNickname ?? '시청자',
          text,
          timestamp: new Date(item.sentAt),
          msgType,
          faqQuestion: action === 'FAQ.ANSWER' ? item.payload?.question : undefined,
        };
      });
      historyLoadedRoomsRef.current.add(roomId);
      setMessages((prev) => [...history, ...prev]);
    } catch {
      // 히스토리 로드 실패는 무시 — 실시간 메시지는 계속 수신됨
    }
  };

  const connect = (roomId: string) => {
    if (!roomId) return;
    if (isConnectingRef.current || clientRef.current) return;
    const SockJS = (window as any).SockJS;
    const StompJs = (window as any).StompJs;
    if (!SockJS || !StompJs) return;
    isConnectingRef.current = true;
    const socket = new SockJS(`${WS_BASE_URL}/ws`);
    const client = StompJs.Stomp.over(socket);
    client.debug = () => {};
    client.heartbeat.outgoing = 5000;
    client.heartbeat.incoming = 5000;
    client.connect({}, () => {
      isConnectingRef.current = false;
      clientRef.current = client;
      setWsConnected(true);
      subRef.current = client.subscribe(`/sub/room/${roomId}`, (msg: any) => appendMessage(msg?.body ?? ''));
      publishJoin(client, roomId);
      loadHistory(roomId);
    }, () => {
      isConnectingRef.current = false;
      setWsConnected(false);
    });
  };

  const sendFaqQuestion = (question: string, productId: number) => {
    if (!campaign?.chatRoomId || !wsConnected || !clientRef.current) return;
    const nick = nickname.trim() || '시청자';
    try {
      clientRef.current.send('/send/room.action', {}, JSON.stringify({
        roomId: campaign.chatRoomId,
        action: 'FAQ.QUESTION',
        actor: { userId: nick, username: nick, sender: nick },
        payload: { question, productId },
      }));
    } catch {
      // 전송 실패는 무시
    }
  };

  const sendMessage = () => {
    if (!inputValue.trim() || !campaign?.chatRoomId) return;
    const nick = nickname.trim() || '시청자';
    const text = inputValue.trim();
    if (wsConnected && clientRef.current) {
      try {
        clientRef.current.send('/send/room.action', {}, JSON.stringify({
          roomId: campaign.chatRoomId, action: 'CHAT.MESSAGE',
          actor: { userId: nick, username: nick, sender: nick },
          payload: { message: text },
        }));
      } catch {
        setMessages((prev) => [...prev, { id: Date.now(), nickname: nick, text, timestamp: new Date() }]);
      }
    }
    setInputValue('');
  };

  // 페이지 진입 시 이미 live 상태이거나 스크립트 로드 완료 시점에 자동 연결 (새로고침 대응)
  useEffect(() => {
    if (!campaign || !areScriptsReady) return;
    const uiStatus = toUiStatus(campaign.status);
    if (uiStatus === 'live' && campaign.chatRoomId && !wsConnected && !isConnectingRef.current) {
      connect(campaign.chatRoomId);
    }
  }, [campaign?.status, campaign?.chatRoomId, areScriptsReady]);

  // 방송 종료 상태로 진입하거나 종료 직후 채팅·FAQ 히스토리 로드 (WS 없이 REST로)
  useEffect(() => {
    if (!campaign?.chatRoomId || toUiStatus(campaign.status) !== 'ended') return;
    loadHistory(campaign.chatRoomId);
  }, [campaign?.chatRoomId, campaign?.status]);

  // 컴포넌트 언마운트 시에만 연결 해제
  useEffect(() => {
    return () => { disconnect(); };
  }, []);

  // 방송 종료 시 채팅·FAQ 집계 조회
  useEffect(() => {
    if (!campaign?.chatRoomId || toUiStatus(campaign.status) !== 'ended') return;
    fetch(`${CHAT_API_BASE}/rooms/${campaign.chatRoomId}/stats`)
      .then((res) => res.json())
      .then((json) => {
        const d = json?.data;
        if (d) setChatStats({
          totalViewers: d.totalViewers,
          peakConcurrent: d.peakConcurrent,
          avgDurationSeconds: d.avgDurationSeconds,
          totalMessages: d.totalMessages,
          faqCount: d.totalQuestions,
        });
      })
      .catch(() => {});
  }, [campaign?.chatRoomId, campaign?.status]);

  // 방송 종료 시 AI 리포트 폴링 (aiReportText 채워질 때까지 최대 12회, 5초 간격)
  const fetchAiReport = useCallback(async () => {
    if (!id) return false;
    try {
      const res = await fetch(`${API_BASE}/campaigns/${id}/reports`);
      if (!res.ok) return false;
      const json = await res.json();
      const data: CampaignReport = json?.data;
      if (data) {
        setAiReport(data);
        if (data.aiReportText) return true; // 완료
      }
    } catch {
      // 리포트 아직 없으면 무시
    }
    return false;
  }, [id]);

  useEffect(() => {
    if (!id || !campaign || toUiStatus(campaign.status) !== 'ended') return;
    if (aiPollRef.current) return; // 이미 폴링 중

    setAiReportLoading(true);
    let attempts = 0;
    const MAX_ATTEMPTS = 12;

    const poll = async () => {
      attempts++;
      const done = await fetchAiReport();
      if (done || attempts >= MAX_ATTEMPTS) {
        clearInterval(aiPollRef.current!);
        aiPollRef.current = null;
        setAiReportLoading(false);
      }
    };

    poll();
    aiPollRef.current = setInterval(poll, 5000);

    return () => {
      if (aiPollRef.current) {
        clearInterval(aiPollRef.current);
        aiPollRef.current = null;
      }
      setAiReportLoading(false);
    };
  }, [id, campaign?.status]);

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
  const isEnded = uiStatus === 'ended';
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

        .pre-faq-card { background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:14px; padding:20px; display:flex; flex-direction:column; gap:14px; }
        .pre-faq-header { display:flex; align-items:baseline; gap:10px; }
        .pre-faq-title { font-size:14px; font-weight:700; color:#f1f5f9; }
        .pre-faq-sub { font-size:11px; color:#64748b; }
        .pre-faq-group { display:flex; flex-direction:column; gap:10px; max-height: 440px; overflow-y:auto; scrollbar-width:thin; scrollbar-color:rgba(255,255,255,0.1) transparent; }
        .pre-faq-group::-webkit-scrollbar { width:4px; }
        .pre-faq-group::-webkit-scrollbar-thumb { background:rgba(255,255,255,0.1); border-radius:2px; }
        .pre-faq-product-name { font-size:11px; font-weight:700; color:#6366f1; letter-spacing:0.04em; padding-bottom:4px; border-bottom:1px solid rgba(99,102,241,0.15); }
        .pre-faq-item { display:flex; flex-direction:column; gap:6px; padding:10px 12px; background:rgba(255,255,255,0.03); border:1px solid rgba(255,255,255,0.06); border-radius:8px; }
        .pre-faq-row { display:flex; align-items:flex-start; gap:8px; }
        .pre-faq-badge { font-size:10px; font-weight:700; padding:2px 7px; border-radius:999px; flex-shrink:0; margin-top:1px; }
        .pre-faq-badge.q { background:rgba(99,102,241,0.2); color:#a5b4fc; border:1px solid rgba(99,102,241,0.3); }
        .pre-faq-badge.a { background:rgba(16,185,129,0.15); color:#6ee7b7; border:1px solid rgba(16,185,129,0.25); }
        .pre-faq-text { font-size:13px; line-height:1.6; }
        .pre-faq-text.q { color:#cbd5e1; font-weight:600; }
        .pre-faq-text.a { color:#94a3b8; }
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

        .timer-badge { position:absolute; bottom:16px; left:16px; background:rgba(0,0,0,0.75); border-radius:999px; padding:5px 14px; font-size:13px; font-weight:700; color:#fca5a5; backdrop-filter:blur(8px); font-variant-numeric:tabular-nums; letter-spacing:0.04em; }

        .product-banner { position:absolute; bottom:0; left:0; right:0; padding:16px; background:linear-gradient(to top, rgba(0,0,0,0.85) 0%, transparent 100%); animation:bannerIn 0.3s ease; }
        @keyframes bannerIn { from { opacity:0; transform:translateY(12px); } to { opacity:1; transform:translateY(0); } }
        .product-banner-inner { display:flex; align-items:center; gap:14px; background:rgba(255,255,255,0.08); backdrop-filter:blur(12px); border:1px solid rgba(255,255,255,0.15); border-radius:12px; padding:12px 16px; }
        .product-banner-left { flex:1; display:flex; flex-direction:column; gap:3px; min-width:0; }
        .product-banner-tag { font-size:10px; font-weight:700; color:#fcd34d; letter-spacing:0.06em; text-transform:uppercase; }
        .product-banner-name { font-size:16px; font-weight:800; color:#f1f5f9; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
        .product-banner-cta { padding:8px 18px; background:linear-gradient(135deg,#f59e0b,#d97706); color:white; border:none; border-radius:8px; font-size:13px; font-weight:700; cursor:pointer; white-space:nowrap; flex-shrink:0; }
        .product-banner-cta:hover { opacity:0.85; }

        .banner-toggle { width:36px; height:20px; border-radius:999px; background:rgba(255,255,255,0.12); border:1px solid rgba(255,255,255,0.15); cursor:pointer; position:relative; transition:background 0.2s, border-color 0.2s; flex-shrink:0; }
        .banner-toggle:hover { background:rgba(255,255,255,0.18); }
        .banner-toggle.on { background:rgba(251,191,36,0.7); border-color:rgba(251,191,36,0.9); }
        .banner-toggle-knob { position:absolute; top:2px; left:2px; width:14px; height:14px; border-radius:50%; background:#fff; transition:transform 0.2s; box-shadow:0 1px 3px rgba(0,0,0,0.4); }
        .banner-toggle.on .banner-toggle-knob { transform:translateX(16px); }

        .broadcast-actions { margin-top:18px; padding-top:18px; border-top:1px solid rgba(255,255,255,0.07); display:flex; gap:10px; }
        .btn-start { flex:1; padding:12px; background:linear-gradient(135deg,#10b981,#059669); color:white; border:none; border-radius:10px; font-size:14px; font-weight:700; cursor:pointer; transition:opacity 0.2s; }
        .btn-start:hover:not(:disabled) { opacity:0.85; }
        .btn-end { flex:1; padding:12px; background:linear-gradient(135deg,#ef4444,#dc2626); color:white; border:none; border-radius:10px; font-size:14px; font-weight:700; cursor:pointer; transition:opacity 0.2s; }
        .btn-end:hover:not(:disabled) { opacity:0.85; }
        .btn-start:disabled, .btn-end:disabled { opacity:0.5; cursor:not-allowed; }
        .btn-sim-run { flex:1; padding:12px; background:linear-gradient(135deg,#6366f1,#4f46e5); color:white; border:none; border-radius:10px; font-size:14px; font-weight:700; cursor:pointer; transition:opacity 0.2s; }
        .btn-sim-run:hover:not(:disabled) { opacity:0.85; }
        .btn-sim-run:disabled { opacity:0.5; cursor:not-allowed; }

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
                  <div className="timer-badge">🔴 {formatElapsed(elapsedSeconds)}</div>
                </>
              )}
              {isScheduled && <div className="status-overlay scheduled">🕐 방송 예정</div>}
              {uiStatus === 'ended' && (
                <div className="status-overlay ended">
                  종료{startedAtLabel && ` · ${startedAtLabel} 시작`}
                </div>
              )}
              {activeBannerProductId && (() => {
                const bp = campaign.campaignProducts.find((p) => p.productId === activeBannerProductId);
                if (!bp) return null;
                return (
                  <div className="product-banner">
                    <div className="product-banner-inner">
                      <div className="product-banner-left">
                        <div className="product-banner-tag">🏷️ 지금 이 상품</div>
                        <div className="product-banner-name">{bp.name}</div>
                      </div>
                      <button className="product-banner-cta">구매하기 →</button>
                    </div>
                  </div>
                );
              })()}
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

              {/* 방송 시작 / 종료 버튼 */}
              {isScheduled && (
                <div className="broadcast-actions">
                  <button className="btn-start" onClick={handleStart} disabled={actionLoading}>
                    {actionLoading ? '처리 중...' : '▶ 방송 시작'}
                  </button>
                </div>
              )}
              {isLive && (
                <div className="broadcast-actions">
                  <button className="btn-end" onClick={handleEnd} disabled={actionLoading}>
                    {actionLoading ? '처리 중...' : '■ 방송 종료'}
                  </button>
                  {process.env.NODE_ENV === 'development' && campaign.chatRoomId && (
                    <button
                      className="btn-sim-run"
                      onClick={runSimulation}
                      disabled={simRunning}
                    >
                      {simRunning ? '시뮬레이션 진행 중...' : '🧪 시뮬레이션 실행'}
                    </button>
                  )}
                </div>
              )}

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
                          <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexShrink: 0 }}>
                            {isLive && (
                              <div
                                className={`banner-toggle ${activeBannerProductId === pid ? 'on' : ''}`}
                                onClick={() => {
                                  if (activeBannerProductId === pid) {
                                    publishBannerOff();
                                  } else {
                                    publishBannerOn(p.productId, p.name);
                                  }
                                }}
                                title={activeBannerProductId === pid ? '배너 끄기' : '배너 켜기'}
                              >
                                <div className="banner-toggle-knob" />
                              </div>
                            )}
                            {uiStatus !== 'ended' && (
                              <button className="btn-remove-product" onClick={() => toggleProduct(pid)}>✕</button>
                            )}
                          </div>
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

            {/* 사전 Q&A 카드 */}
            {productFaqGroups.length > 0 && (
              <div className="pre-faq-card">
                <div className="pre-faq-header">
                  <span className="pre-faq-title">📋 방송 전 사전 Q&A</span>
                  <span className="pre-faq-sub">방송 전 준비된 예상 질문과 답변입니다</span>
                </div>
                {productFaqGroups.map((group) => (
                  <div key={group.productId} className="pre-faq-group">
                    {productFaqGroups.length > 1 && (
                      <div className="pre-faq-product-name">{group.productName}</div>
                    )}
                    {group.items.map((item) => (
                      <div key={item.id} className="pre-faq-item">
                        <div className="pre-faq-row">
                          <span className="pre-faq-badge q">Q</span>
                          <span className="pre-faq-text q">{item.question}</span>
                        </div>
                        <div className="pre-faq-row">
                          <span className="pre-faq-badge a">A</span>
                          <span className="pre-faq-text a">{item.answer}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* 우측 패널: 상태별 */}
          <div className="side-col">
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
              onSendFaq={sendFaqQuestion}
              defaultProductId={campaign.campaignProducts[0]?.productId}
              chatStats={chatStats}
              aiReport={aiReport}
              aiReportLoading={aiReportLoading}
            />
          </div>
        </div>
      </div>
    </>
  );
}
