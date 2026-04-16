import React, { useEffect, useRef, useState } from 'react';
import Head from 'next/head';
import Script from 'next/script';

export default function StompTest() {
  const [isConnected, setIsConnected] = useState(false);
  const [isSubscribed, setIsSubscribed] = useState(false);
  const stompClientRef = useRef<any>(null);
  const subscriptionRef = useRef<any>(null);
  const scriptsLoadedRef = useRef(false);

  const log = (message: string, type: string = 'info') => {
    const logElement = document.getElementById('log');
    if (!logElement) return;

    const timestamp = new Date().toLocaleTimeString();
    const entry = document.createElement('div');
    entry.className = `log-entry ${type}`;
    entry.textContent = `[${timestamp}] ${message}`;
    logElement.appendChild(entry);
    logElement.scrollTop = logElement.scrollHeight;
  };

  const updateStatus = (connected: boolean) => {
    setIsConnected(connected);
    const statusElement = document.getElementById('status');
    const connectBtn = document.getElementById('connectBtn') as HTMLButtonElement;
    const disconnectBtn = document.getElementById('disconnectBtn') as HTMLButtonElement;
    const subscribeBtn = document.getElementById('subscribeBtn') as HTMLButtonElement;

    if (connected) {
      if (statusElement) {
        statusElement.textContent = '연결됨 ✅';
        statusElement.className = 'status connected';
      }
      if (connectBtn) connectBtn.disabled = true;
      if (disconnectBtn) disconnectBtn.disabled = false;
      if (subscribeBtn) subscribeBtn.disabled = false;
    } else {
      if (statusElement) {
        statusElement.textContent = '연결 안 됨 ❌';
        statusElement.className = 'status disconnected';
      }
      if (connectBtn) connectBtn.disabled = false;
      if (disconnectBtn) disconnectBtn.disabled = true;
      if (subscribeBtn) subscribeBtn.disabled = true;
      setIsSubscribed(false);
    }
  };

  const getFormValues = () => {
    const roomId = (document.getElementById('roomId') as HTMLInputElement)?.value || 'ROOM_1';
    const userId = (document.getElementById('userId') as HTMLInputElement)?.value || 'user-1';
    const sender = (document.getElementById('sender') as HTMLInputElement)?.value || '익명';
    const message = (document.getElementById('message') as HTMLInputElement)?.value || '';
    return { roomId, userId, sender, message };
  };

  const buildEnvelope = (action: string, roomId: string, userId: string, sender: string, payload: Record<string, any>) => ({
    roomId,
    action,
    actor: { userId, username: `${userId}@test.com`, sender },
    payload,
  });

  const connect = () => {
    log('WebSocket 연결 시도 중...', 'info');

    const SockJS = (window as any).SockJS;
    const StompJs = (window as any).StompJs;

    if (!SockJS || !StompJs) {
      log('❌ SockJS 또는 STOMP 라이브러리가 로드되지 않았습니다.', 'error');
      return;
    }

    const wsUrl = process.env.NEXT_PUBLIC_WS_URL || 'http://localhost:8080';
    const wsEndpoint = `${wsUrl}/ws`;
    log(`연결 URL: ${wsEndpoint}`, 'info');

    const socket = new SockJS(wsEndpoint);
    const client = StompJs.Stomp.over(socket);

    client.debug = () => {};

    client.connect(
      {},
      function (frame: any) {
        log('✅ WebSocket 연결 성공!', 'success');
        log('연결 정보: ' + frame, 'info');
        stompClientRef.current = client;
        updateStatus(true);
      },
      function (error: any) {
        log('❌ 연결 실패: ' + error, 'error');
        updateStatus(false);
      }
    );
  };

  const disconnect = () => {
    if (stompClientRef.current !== null) {
      if (subscriptionRef.current) {
        subscriptionRef.current.unsubscribe();
        subscriptionRef.current = null;
        setIsSubscribed(false);
        log('구독 해제됨', 'info');
      }
      stompClientRef.current.disconnect();
      log('연결 끊김', 'info');
      stompClientRef.current = null;
      updateStatus(false);
    }
  };

  const subscribe = () => {
    if (!isConnected || !stompClientRef.current) {
      log('❌ 먼저 연결하세요!', 'error');
      return;
    }

    const { roomId } = getFormValues();
    const destination = `/sub/room/${roomId}`;

    if (isSubscribed && subscriptionRef.current) {
      subscriptionRef.current.unsubscribe();
      log('기존 구독 해제', 'info');
    }

    subscriptionRef.current = stompClientRef.current.subscribe(destination, function (frame: any) {
      try {
        const data = JSON.parse(frame.body);
        const actor = data.actor?.sender ?? '시스템';
        switch (data.action) {
          case 'CHAT.MESSAGE':
            log(`💬 [${actor}]: ${data.payload?.message}`, 'received');
            break;
          case 'CHAT.JOIN':
            log(`➡️ ${actor} 님이 입장했습니다.`, 'success');
            break;
          case 'CHAT.LEAVE':
            log(`⬅️ ${actor} 님이 퇴장했습니다.`, 'info');
            break;
          case 'CHAT.SYSTEM':
            log(`📢 시스템: ${data.payload?.message}`, 'info');
            break;
          default:
            log(`📩 알 수 없는 액션(${data.action}): ${frame.body}`, 'received');
        }
      } catch (e) {
        log('📩 수신 (파싱 실패): ' + frame.body, 'received');
      }
    });

    setIsSubscribed(true);
    log(`✅ 구독 완료: ${destination}`, 'success');
  };

  const sendAction = (action: string) => {
    if (!isConnected || !stompClientRef.current) {
      log('❌ 먼저 연결하세요!', 'error');
      return;
    }

    const { roomId, userId, sender, message } = getFormValues();

    if (action === 'CHAT.MESSAGE' && !message.trim()) {
      log('❌ 메시지를 입력하세요!', 'error');
      return;
    }
    if (action === 'CHAT.SYSTEM' && !message.trim()) {
      log('❌ 시스템 메시지를 입력하세요!', 'error');
      return;
    }

    const payload = (action === 'CHAT.MESSAGE' || action === 'CHAT.SYSTEM')
      ? { message }
      : {};

    const envelope = buildEnvelope(action, roomId, userId, sender, payload);
    stompClientRef.current.send('/send/room.action', {}, JSON.stringify(envelope));
    log(`➡️ [${action}] 전송: ${JSON.stringify(envelope)}`, 'info');

    const messageInput = document.getElementById('message') as HTMLInputElement;
    if (messageInput) messageInput.value = '';
  };

  const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      sendAction('CHAT.MESSAGE');
    }
  };

  useEffect(() => {
    const checkScripts = setInterval(() => {
      if (typeof window !== 'undefined' && (window as any).SockJS && (window as any).StompJs) {
        if (!scriptsLoadedRef.current) {
          scriptsLoadedRef.current = true;
          clearInterval(checkScripts);
          log('STOMP WebSocket 테스트 페이지 준비됨', 'info');
          log('1. "연결" 후 "구독" 버튼을 클릭하세요', 'info');
          log('2. JOIN / LEAVE / 메시지 전송 / SYSTEM 버튼을 사용하세요', 'info');
        }
      }
    }, 100);

    return () => clearInterval(checkScripts);
  }, []);

  const actionDisabled = !isConnected || !isSubscribed;

  return (
    <>
      <Head>
        <title>STOMP WebSocket 테스트</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      </Head>

      <Script
        src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"
        strategy="lazyOnload"
      />
      <Script
        src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js"
        strategy="lazyOnload"
      />

      <style jsx global>{`
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
          background: linear-gradient(135deg, #f0f4f8 0%, #e2e8f0 100%);
          min-height: 100vh;
          padding: 20px;
        }

        .container {
          max-width: 1200px;
          margin: 0 auto;
          background: white;
          border-radius: 12px;
          box-shadow: 0 10px 40px rgba(0,0,0,0.1);
          overflow: hidden;
        }

        .header {
          background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
          color: white;
          padding: 30px;
          text-align: center;
        }

        .header h1 { font-size: 28px; margin-bottom: 10px; }

        .status {
          display: inline-block;
          padding: 8px 16px;
          border-radius: 20px;
          font-size: 14px;
          font-weight: 600;
          margin-top: 10px;
        }

        .status.connected { background: #10b981; }
        .status.disconnected { background: #ef4444; }

        .content {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 20px;
          padding: 30px;
        }

        .panel {
          background: #f9fafb;
          border-radius: 8px;
          padding: 20px;
        }

        .panel h2 {
          font-size: 20px;
          margin-bottom: 20px;
          color: #1f2937;
          border-bottom: 2px solid #e5e7eb;
          padding-bottom: 10px;
        }

        .form-group { margin-bottom: 15px; }

        .form-group label {
          display: block;
          font-size: 14px;
          font-weight: 600;
          color: #374151;
          margin-bottom: 5px;
        }

        .form-group input {
          width: 100%;
          padding: 10px;
          border: 1px solid #d1d5db;
          border-radius: 6px;
          font-size: 14px;
          transition: border-color 0.2s;
        }

        .form-group input:focus {
          outline: none;
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .button-group { display: flex; gap: 10px; margin-top: 12px; }

        .action-group {
          margin-top: 20px;
          padding-top: 16px;
          border-top: 1px solid #e5e7eb;
        }

        .action-group-label {
          font-size: 12px;
          font-weight: 700;
          color: #6b7280;
          text-transform: uppercase;
          letter-spacing: 0.05em;
          margin-bottom: 10px;
        }

        button {
          flex: 1;
          padding: 11px 16px;
          border: none;
          border-radius: 6px;
          font-size: 13px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.2s;
        }

        button:hover:not(:disabled) {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        button:active:not(:disabled) { transform: translateY(0); }

        .btn-connect { background: #10b981; color: white; }
        .btn-disconnect { background: #ef4444; color: white; }
        .btn-subscribe { background: #3b82f6; color: white; }
        .btn-join { background: #059669; color: white; }
        .btn-leave { background: #f59e0b; color: white; }
        .btn-send { background: #8b5cf6; color: white; }
        .btn-system { background: #64748b; color: white; }

        button:disabled {
          background: #d1d5db;
          color: #9ca3af;
          cursor: not-allowed;
          transform: none;
        }

        .log {
          background: #1f2937;
          color: #10b981;
          padding: 15px;
          border-radius: 6px;
          font-family: 'Courier New', monospace;
          font-size: 13px;
          height: 480px;
          overflow-y: auto;
          white-space: pre-wrap;
          word-wrap: break-word;
        }

        .log-entry { margin-bottom: 5px; line-height: 1.6; }
        .log-entry.info { color: #60a5fa; }
        .log-entry.success { color: #10b981; }
        .log-entry.error { color: #f87171; }
        .log-entry.received { color: #fbbf24; }

        @media (max-width: 768px) {
          .content { grid-template-columns: 1fr; }
        }
      `}</style>

      <div className="container">
        <div className="header">
          <h1>STOMP WebSocket 테스트</h1>
          <div id="status" className="status disconnected">연결 안 됨</div>
        </div>

        <div className="content">
          <div className="panel">
            <h2>설정 & 전송</h2>

            <div className="form-group">
              <label htmlFor="roomId">Room ID</label>
              <input type="text" id="roomId" defaultValue="ROOM_1" placeholder="ROOM_1" />
            </div>
            <div className="form-group">
              <label htmlFor="userId">User ID</label>
              <input type="text" id="userId" defaultValue="user-1" placeholder="user-1" />
            </div>
            <div className="form-group">
              <label htmlFor="sender">표시 이름</label>
              <input type="text" id="sender" defaultValue="테스트유저" placeholder="테스트유저" />
            </div>
            <div className="form-group">
              <label htmlFor="message">메시지 (CHAT.MESSAGE / CHAT.SYSTEM)</label>
              <input
                type="text"
                id="message"
                placeholder="메시지를 입력하세요"
                onKeyPress={handleKeyPress}
              />
            </div>

            {/* 연결 제어 */}
            <div className="button-group">
              <button id="connectBtn" className="btn-connect" onClick={connect}>
                연결
              </button>
              <button
                id="disconnectBtn"
                className="btn-disconnect"
                onClick={disconnect}
                disabled={!isConnected}
              >
                연결 끊기
              </button>
              <button
                id="subscribeBtn"
                className="btn-subscribe"
                onClick={subscribe}
                disabled={!isConnected}
              >
                구독
              </button>
            </div>

            {/* 액션 버튼 */}
            <div className="action-group">
              <div className="action-group-label">액션 전송 (구독 후 활성화)</div>
              <div className="button-group">
                <button
                  className="btn-join"
                  onClick={() => sendAction('CHAT.JOIN')}
                  disabled={actionDisabled}
                >
                  JOIN
                </button>
                <button
                  className="btn-leave"
                  onClick={() => sendAction('CHAT.LEAVE')}
                  disabled={actionDisabled}
                >
                  LEAVE
                </button>
              </div>
              <div className="button-group">
                <button
                  className="btn-send"
                  onClick={() => sendAction('CHAT.MESSAGE')}
                  disabled={actionDisabled}
                >
                  메시지 전송
                </button>
                <button
                  className="btn-system"
                  onClick={() => sendAction('CHAT.SYSTEM')}
                  disabled={actionDisabled}
                >
                  SYSTEM
                </button>
              </div>
            </div>
          </div>

          <div className="panel">
            <h2>메시지 로그</h2>
            <div id="log" className="log"></div>
          </div>
        </div>
      </div>
    </>
  );
}
