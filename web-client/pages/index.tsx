import React, { useState, useRef, useEffect } from 'react';

interface ChatRoomProps {
  title: string;
  isMain?: boolean;
  nickname?: string;
}

interface Message {
  id: number;
  nickname: string;
  text: string;
  timestamp: Date;
}

const ChatRoom: React.FC<ChatRoomProps> = ({ title, isMain = false, nickname: propNickname }) => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [nickname, setNickname] = useState(propNickname || '사용자');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = () => {
    if (inputValue.trim()) {
      const newMessage: Message = {
        id: Date.now(),
        nickname: nickname.trim() || '사용자',
        text: inputValue.trim(),
        timestamp: new Date(),
      };
      setMessages([...messages, newMessage]);
      setInputValue('');
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSend();
    }
  };

  return (
    <div className={`chat-room ${isMain ? 'main' : ''}`}>
      <div className={`chat-room-header ${isMain ? 'main' : ''}`}>
        {nickname}의 채팅창
      </div>
      <div className="chat-room-messages">
        {messages.length === 0 ? (
          <div className="chat-room-empty">채팅 메시지가 여기에 표시됩니다...</div>
        ) : (
          <>
            {messages.map((msg) => (
              <div key={msg.id} className="chat-message">
                <div className="chat-message-content">
                  <span className="chat-message-nickname">{msg.nickname}</span>
                  <span className="chat-message-separator">|</span>
                  <span className="chat-message-text">{msg.text}</span>
                  <span className="chat-message-time">{msg.timestamp.toLocaleTimeString()}</span>
                </div>
              </div>
            ))}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>
      <div className="chat-room-input">
        <input
          type="text"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="메시지를 입력하세요..."
          className="chat-input"
        />
        <button onClick={handleSend} className={`chat-send-btn ${isMain ? 'main' : ''}`}>
          전송
        </button>
      </div>
    </div>
  );
};

export default function Home() {
  const [selectedChatRoom, setSelectedChatRoom] = useState('메인 채팅방');
  const [chatRooms, setChatRooms] = useState(['메인 채팅방', '서브 채팅방 1', '서브 채팅방 2', '서브 채팅방 3', '서브 채팅방 4', '서브 채팅방 5', '서브 채팅방 6', '서브 채팅방 7', '서브 채팅방 8', '서브 채팅방 9']);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newChatRoomTitle, setNewChatRoomTitle] = useState('');

  const handleCreateChatRoom = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setNewChatRoomTitle('');
  };

  const handleSaveChatRoom = () => {
    if (newChatRoomTitle.trim()) {
      setChatRooms([...chatRooms, newChatRoomTitle.trim()]);
      setSelectedChatRoom(newChatRoomTitle.trim());
      handleCloseModal();
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSaveChatRoom();
    }
  };

  return (
    <>
      <style jsx global>{`
        * {
          margin: 0;
          padding: 0;
          box-sizing: border-box;
        }
        
        body {
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
          background: linear-gradient(135deg, #f0f4f8 0%, #e2e8f0 100%);
          min-height: 100vh;
          padding: 20px;
        }
        
        .main-container {
          max-width: 1400px;
          margin: 0 auto;
        }
        
        .page-header {
          background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
          color: white;
          padding: 30px;
          text-align: center;
          border-radius: 12px;
          box-shadow: 0 10px 40px rgba(0,0,0,0.1);
          margin-bottom: 30px;
        }
        
        .page-header h1 {
          font-size: 28px;
          margin: 0;
        }
        
        .main-content {
          display: flex;
          gap: 20px;
          margin-bottom: 30px;
          align-items: flex-start;
        }
        
        .main-chat-wrapper {
          width: calc(66.67% - 6.67px);
        }
        
        .control-panel {
          flex: 1;
          background: white;
          border-radius: 12px;
          padding: 20px;
          box-shadow: 0 10px 40px rgba(0,0,0,0.1);
          min-height: 400px;
        }
        
        .control-panel-title {
          font-size: 20px;
          font-weight: 600;
          color: #1f2937;
          margin-bottom: 15px;
          border-bottom: 2px solid #e5e7eb;
          padding-bottom: 10px;
        }
        
        .control-panel-controls {
          display: flex;
          gap: 10px;
          align-items: center;
        }
        
        .control-select {
          flex: 1;
          padding: 8px 12px;
          border: 1px solid #d1d5db;
          border-radius: 6px;
          font-size: 13px;
          background: white;
          cursor: pointer;
          outline: none;
          transition: border-color 0.2s;
        }
        
        .control-select:focus {
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .btn-create {
          padding: 8px 16px;
          background: #10b981;
          color: white;
          border: none;
          border-radius: 6px;
          font-size: 13px;
          font-weight: 600;
          cursor: pointer;
          white-space: nowrap;
          transition: all 0.2s;
        }
        
        .btn-create:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0,0,0,0.15);
          background: #059669;
        }
        
        .sub-chat-grid {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 20px;
        }
        
        .chat-room {
          background: white;
          border-radius: 12px;
          box-shadow: 0 10px 40px rgba(0,0,0,0.1);
          display: flex;
          flex-direction: column;
          height: 200px;
          overflow: hidden;
        }
        
        .chat-room.main {
          height: 400px;
        }
        
        .chat-room-header {
          padding: 12px 16px;
          background: #f9fafb;
          color: #1f2937;
          font-weight: 600;
          font-size: 14px;
          border-bottom: 1px solid #e5e7eb;
        }
        
        .chat-room-header.main {
          background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
          color: white;
          font-size: 18px;
        }
        
        .chat-room-messages {
          flex: 1;
          padding: 12px;
          overflow-y: auto;
          display: flex;
          flex-direction: column;
          gap: 8px;
          font-size: 11px;
          color: #1f2937;
        }
        
        .chat-room.main .chat-room-messages {
          font-size: 14px;
        }
        
        .chat-room-empty {
          color: #9ca3af;
          font-style: italic;
          font-size: 10px;
          text-align: center;
          margin-top: 20px;
        }
        
        .chat-room.main .chat-room-empty {
          font-size: 13px;
        }
        
        .chat-message {
          padding: 8px 12px;
          background: #f9fafb;
          border-radius: 6px;
          word-break: break-word;
        }
        
        .chat-message-content {
          display: flex;
          align-items: center;
          gap: 8px;
          flex-wrap: wrap;
          font-size: 11px;
        }
        
        .chat-room.main .chat-message-content {
          font-size: 13px;
        }
        
        .chat-message-nickname {
          font-weight: 600;
          color: #667eea;
        }
        
        .chat-message-separator {
          color: #9ca3af;
        }
        
        .chat-message-text {
          flex: 1;
        }
        
        .chat-message-time {
          font-size: 10px;
          color: #9ca3af;
        }
        
        .chat-room.main .chat-message-time {
          font-size: 10px;
        }
        
        .chat-room-input {
          border-top: 1px solid #e5e7eb;
          padding: 12px;
          display: flex;
          gap: 8px;
        }
        
        .chat-input {
          flex: 1;
          padding: 10px;
          border: 1px solid #d1d5db;
          border-radius: 6px;
          font-size: 14px;
          outline: none;
          transition: border-color 0.2s;
        }
        
        .chat-input:focus {
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .chat-send-btn {
          padding: 10px 20px;
          background: #6c757d;
          color: white;
          border: none;
          border-radius: 6px;
          font-size: 14px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.2s;
        }
        
        .chat-send-btn.main {
          background: #6366f1;
        }
        
        .chat-send-btn:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        
        .chat-send-btn.main:hover {
          background: #4f46e5;
        }
        
        .chat-send-btn:not(.main):hover {
          background: #5a6268;
        }
        
        .modal-overlay {
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.5);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 1000;
        }
        
        .modal-content {
          background: white;
          border-radius: 12px;
          padding: 30px;
          width: 90%;
          max-width: 500px;
          box-shadow: 0 10px 40px rgba(0,0,0,0.1);
        }
        
        .modal-title {
          font-size: 24px;
          font-weight: 600;
          margin-bottom: 20px;
          color: #1f2937;
        }
        
        .modal-form-group {
          margin-bottom: 20px;
        }
        
        .modal-label {
          display: block;
          margin-bottom: 8px;
          font-size: 14px;
          font-weight: 600;
          color: #374151;
        }
        
        .modal-input {
          width: 100%;
          padding: 12px;
          border: 1px solid #d1d5db;
          border-radius: 6px;
          font-size: 14px;
          outline: none;
          transition: border-color 0.2s;
        }
        
        .modal-input:focus {
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .modal-actions {
          display: flex;
          gap: 10px;
          justify-content: flex-end;
        }
        
        .btn-cancel {
          padding: 10px 20px;
          background: #6c757d;
          color: white;
          border: none;
          border-radius: 6px;
          font-size: 14px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.2s;
        }
        
        .btn-cancel:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0,0,0,0.15);
          background: #5a6268;
        }
        
        .btn-save {
          padding: 10px 20px;
          background: #10b981;
          color: white;
          border: none;
          border-radius: 6px;
          font-size: 14px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.2s;
        }
        
        .btn-save:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0,0,0,0.15);
          background: #059669;
        }
        
        @media (max-width: 768px) {
          .main-content {
            flex-direction: column;
          }
          
          .main-chat-wrapper {
            width: 100%;
          }
          
          .sub-chat-grid {
            grid-template-columns: 1fr;
          }
        }
      `}</style>

      <div className="main-container">
        <div className="page-header">
          <h1>🚀 Live Platform Lab - 채팅방</h1>
        </div>

        <div className="main-content">
          <div className="main-chat-wrapper">
            <ChatRoom title={selectedChatRoom} isMain={true} nickname="메인 사용자" />
          </div>

          <div className="control-panel">
            <div className="control-panel-title">채팅방 선택</div>
            <div className="control-panel-controls">
              <select
                value={selectedChatRoom}
                onChange={(e) => setSelectedChatRoom(e.target.value)}
                className="control-select"
              >
                {chatRooms.map((room) => (
                  <option key={room} value={room}>
                    {room}
                  </option>
                ))}
              </select>
              <button onClick={handleCreateChatRoom} className="btn-create">
                생성
              </button>
            </div>
          </div>
        </div>

        <div className="sub-chat-grid">
          {Array.from({ length: 9 }, (_, i) => (
            <ChatRoom key={i} title={`서브 채팅방 ${i + 1}`} nickname={`사용자${i + 1}`} />
          ))}
        </div>
      </div>

      {isModalOpen && (
        <div className="modal-overlay" onClick={handleCloseModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">채팅방 생성</h2>
            <div className="modal-form-group">
              <label className="modal-label">채팅방 제목</label>
              <input
                type="text"
                value={newChatRoomTitle}
                onChange={(e) => setNewChatRoomTitle(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="채팅방 제목을 입력하세요"
                className="modal-input"
                autoFocus
              />
            </div>
            <div className="modal-actions">
              <button onClick={handleCloseModal} className="btn-cancel">
                취소
              </button>
              <button onClick={handleSaveChatRoom} className="btn-save">
                저장
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
