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
    const sendBtn = document.getElementById('sendBtn') as HTMLButtonElement;

    if (connected) {
      if (statusElement) {
        statusElement.textContent = '연결됨 ✅';
        statusElement.className = 'status connected';
      }
      if (connectBtn) connectBtn.disabled = true;
      if (disconnectBtn) disconnectBtn.disabled = false;
      if (subscribeBtn) subscribeBtn.disabled = false;
      if (sendBtn) sendBtn.disabled = false;
    } else {
      if (statusElement) {
        statusElement.textContent = '연결 안 됨 ❌';
        statusElement.className = 'status disconnected';
      }
      if (connectBtn) connectBtn.disabled = false;
      if (disconnectBtn) disconnectBtn.disabled = true;
      if (subscribeBtn) subscribeBtn.disabled = true;
      if (sendBtn) sendBtn.disabled = true;
      setIsSubscribed(false);
    }
  };

  const connect = () => {
    log('WebSocket 연결 시도 중...', 'info');
    
    const SockJS = (window as any).SockJS;
    const StompJs = (window as any).StompJs;
    
    if (!SockJS || !StompJs) {
      log('❌ SockJS 또는 STOMP 라이브러리가 로드되지 않았습니다.', 'error');
      return;
    }
    
    const socket = new SockJS('http://localhost:8080/ws');
    const client = StompJs.Stomp.over(socket);
    
    client.debug = () => {
      // 디버그 로그 비활성화
    };

    client.connect({}, 
      function(frame: any) {
        log('✅ WebSocket 연결 성공!', 'success');
        log('연결 정보: ' + frame, 'info');
        stompClientRef.current = client;
        updateStatus(true);
      },
      function(error: any) {
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

    const channelIdInput = document.getElementById('channelId') as HTMLInputElement;
    const channelId = channelIdInput?.value || '1';
    const destination = `/sub/channel/${channelId}`;

    if (isSubscribed && subscriptionRef.current) {
      subscriptionRef.current.unsubscribe();
      log('기존 구독 해제: ' + destination, 'info');
    }

    subscriptionRef.current = stompClientRef.current.subscribe(destination, function(message: any) {
      try {
        const data = JSON.parse(message.body);
        log(`📩 메시지 수신 [Channel ${data.channelId}]: ${data.sender}: ${data.message}`, 'received');
      } catch (e) {
        log('📩 메시지 수신: ' + message.body, 'received');
      }
    });

    setIsSubscribed(true);
    log(`✅ 구독 완료: ${destination}`, 'success');
  };

  const sendMessage = () => {
    if (!isConnected || !stompClientRef.current) {
      log('❌ 먼저 연결하세요!', 'error');
      return;
    }

    const channelIdInput = document.getElementById('channelId') as HTMLInputElement;
    const senderInput = document.getElementById('sender') as HTMLInputElement;
    const messageInput = document.getElementById('message') as HTMLInputElement;

    const channelId = channelIdInput?.value;
    const sender = senderInput?.value;
    const message = messageInput?.value;

    if (!channelId || !sender || !message) {
      log('❌ 모든 필드를 입력하세요!', 'error');
      return;
    }

    const messageData = {
      channelId: parseInt(channelId),
      sender: sender,
      message: message
    };

    stompClientRef.current.send('/pub/chat.send', {}, JSON.stringify(messageData));
    log(`➡️ 메시지 전송: ${JSON.stringify(messageData)}`, 'info');
    
    if (messageInput) messageInput.value = '';
  };

  const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      sendMessage();
    }
  };

  useEffect(() => {
    // 스크립트가 로드될 때까지 대기
    const checkScripts = setInterval(() => {
      if (typeof window !== 'undefined' && (window as any).SockJS && (window as any).StompJs) {
        if (!scriptsLoadedRef.current) {
          scriptsLoadedRef.current = true;
          clearInterval(checkScripts);
          
          // 초기 로그
          log('STOMP WebSocket 테스트 페이지 준비됨', 'info');
          log('1. "연결" 버튼을 클릭하세요', 'info');
          log('2. "구독" 버튼을 클릭하세요', 'info');
          log('3. 메시지를 입력하고 "전송" 버튼을 클릭하세요', 'info');
        }
      }
    }, 100);

    return () => clearInterval(checkScripts);
  }, []);

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
        * {
          margin: 0;
          padding: 0;
          box-sizing: border-box;
        }
        
        body {
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          padding: 30px;
          text-align: center;
        }
        
        .header h1 {
          font-size: 28px;
          margin-bottom: 10px;
        }
        
        .status {
          display: inline-block;
          padding: 8px 16px;
          border-radius: 20px;
          font-size: 14px;
          font-weight: 600;
          margin-top: 10px;
        }
        
        .status.connected {
          background: #10b981;
        }
        
        .status.disconnected {
          background: #ef4444;
        }
        
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
        
        .form-group {
          margin-bottom: 15px;
        }
        
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
        
        .button-group {
          display: flex;
          gap: 10px;
          margin-top: 20px;
        }
        
        button {
          flex: 1;
          padding: 12px 24px;
          border: none;
          border-radius: 6px;
          font-size: 14px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.2s;
        }
        
        button:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        
        button:active {
          transform: translateY(0);
        }
        
        .btn-connect {
          background: #10b981;
          color: white;
        }
        
        .btn-disconnect {
          background: #ef4444;
          color: white;
        }
        
        .btn-subscribe {
          background: #3b82f6;
          color: white;
        }
        
        .btn-send {
          background: #8b5cf6;
          color: white;
        }
        
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
          max-height: 400px;
          overflow-y: auto;
          white-space: pre-wrap;
          word-wrap: break-word;
        }
        
        .log-entry {
          margin-bottom: 5px;
          line-height: 1.6;
        }
        
        .log-entry.info {
          color: #60a5fa;
        }
        
        .log-entry.success {
          color: #10b981;
        }
        
        .log-entry.error {
          color: #f87171;
        }
        
        .log-entry.received {
          color: #fbbf24;
        }
        
        @media (max-width: 768px) {
          .content {
            grid-template-columns: 1fr;
          }
        }
      `}</style>

      <div className="container">
        <div className="header">
          <h1>🚀 STOMP WebSocket 테스트</h1>
          <div id="status" className="status disconnected">연결 안 됨</div>
        </div>
        
        <div className="content">
          <div className="panel">
            <h2>📤 메시지 전송</h2>
            <div className="form-group">
              <label htmlFor="channelId">Channel ID</label>
              <input type="number" id="channelId" defaultValue="1" min="1" />
            </div>
            <div className="form-group">
              <label htmlFor="sender">보낸 사람</label>
              <input type="text" id="sender" defaultValue="test-user" placeholder="이름을 입력하세요" />
            </div>
            <div className="form-group">
              <label htmlFor="message">메시지</label>
              <input 
                type="text" 
                id="message" 
                placeholder="메시지를 입력하세요" 
                onKeyPress={handleKeyPress}
              />
            </div>
            <div className="button-group">
              <button 
                id="connectBtn" 
                className="btn-connect" 
                onClick={connect}
              >
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
            </div>
            <div className="button-group">
              <button 
                id="subscribeBtn" 
                className="btn-subscribe" 
                onClick={subscribe}
                disabled={!isConnected}
              >
                구독
              </button>
              <button 
                id="sendBtn" 
                className="btn-send" 
                onClick={sendMessage}
                disabled={!isConnected}
              >
                전송
              </button>
            </div>
          </div>
          
          <div className="panel">
            <h2>📥 메시지 로그</h2>
            <div id="log" className="log"></div>
          </div>
        </div>
      </div>
    </>
  );
}
