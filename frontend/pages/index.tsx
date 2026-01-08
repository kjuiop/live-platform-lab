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
    <div style={{
      border: '2px solid #e0e0e0',
      borderRadius: '8px',
      display: 'flex',
      flexDirection: 'column',
      backgroundColor: '#ffffff',
      boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
      height: isMain ? '400px' : '200px',
    }}>
      <div style={{
        padding: '12px 16px',
        borderBottom: '1px solid #e0e0e0',
        backgroundColor: isMain ? '#4a90e2' : '#f5f5f5',
        color: isMain ? '#ffffff' : '#333333',
        fontWeight: 'bold',
        fontSize: isMain ? '18px' : '14px',
        borderRadius: '6px 6px 0 0',
      }}>
        {nickname}의 채팅창
      </div>
      <div style={{
        flex: 1,
        padding: '12px',
        overflowY: 'auto',
        fontSize: isMain ? '14px' : '11px',
        color: '#333333',
        display: 'flex',
        flexDirection: 'column',
        gap: '8px',
      }}>
        {messages.length === 0 ? (
          <div style={{ color: '#999999', fontStyle: 'italic', fontSize: isMain ? '13px' : '10px' }}>
            채팅 메시지가 여기에 표시됩니다...
          </div>
        ) : (
          <>
            {messages.map((msg) => (
              <div key={msg.id} style={{
                padding: '6px 10px',
                backgroundColor: '#f0f0f0',
                borderRadius: '6px',
                wordBreak: 'break-word',
              }}>
                <div style={{ 
                  fontSize: isMain ? '13px' : '11px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  flexWrap: 'wrap',
                }}>
                  <span style={{ 
                    fontWeight: 'bold', 
                    color: '#4a90e2' 
                  }}>
                    {msg.nickname}
                  </span>
                  <span style={{ color: '#999999' }}>|</span>
                  <span style={{ flex: 1 }}>{msg.text}</span>
                  <span style={{ 
                    fontSize: isMain ? '10px' : '8px', 
                    color: '#999999' 
                  }}>
                    {msg.timestamp.toLocaleTimeString()}
                  </span>
                </div>
              </div>
            ))}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>
      <div style={{
        borderTop: '1px solid #e0e0e0',
        padding: '8px',
        display: 'flex',
        gap: '8px',
      }}>
        <input
          type="text"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="메시지를 입력하세요..."
          style={{
            flex: 1,
            padding: isMain ? '10px' : '6px',
            border: '1px solid #d0d0d0',
            borderRadius: '4px',
            fontSize: isMain ? '14px' : '12px',
            outline: 'none',
          }}
        />
        <button
          onClick={handleSend}
          style={{
            padding: isMain ? '10px 20px' : '6px 16px',
            backgroundColor: isMain ? '#4a90e2' : '#6c757d',
            color: '#ffffff',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: isMain ? '14px' : '12px',
            fontWeight: 'bold',
          }}
          onMouseOver={(e) => {
            e.currentTarget.style.backgroundColor = isMain ? '#357abd' : '#5a6268';
          }}
          onMouseOut={(e) => {
            e.currentTarget.style.backgroundColor = isMain ? '#4a90e2' : '#6c757d';
          }}
        >
          전송
        </button>
      </div>
    </div>
  );
};

export default function Home() {
  const [selectedChatRoom, setSelectedChatRoom] = useState('메인 채팅방');
  const chatRooms = ['메인 채팅방', '서브 채팅방 1', '서브 채팅방 2', '서브 채팅방 3', '서브 채팅방 4', '서브 채팅방 5', '서브 채팅방 6', '서브 채팅방 7', '서브 채팅방 8', '서브 채팅방 9'];

  const handleCreateChatRoom = () => {
    // 채팅방 생성 로직 (추후 구현)
    alert('채팅방 생성 기능은 추후 구현 예정입니다.');
  };

  return (
    <div style={{
      minHeight: '100vh',
      fontFamily: 'system-ui, -apple-system, sans-serif',
      backgroundColor: '#f0f0f0',
      padding: '20px',
    }}>
      <div style={{
        maxWidth: '1400px',
        margin: '0 auto',
      }}>
        <h1 style={{
          fontSize: '2rem',
          marginBottom: '20px',
          color: '#333',
          textAlign: 'center',
        }}>
          Live Platform Lab - 채팅방
        </h1>

        {/* 메인 채팅방과 컨트롤 카드 */}
        <div style={{
          marginBottom: '30px',
          display: 'flex',
          gap: '20px',
          alignItems: 'flex-start',
        }}>
          {/* 메인 채팅방 */}
          <div style={{
            width: '70%',
          }}>
            <ChatRoom title={selectedChatRoom} isMain={true} nickname="메인 사용자" />
          </div>

          {/* 컨트롤 카드 */}
          <div style={{
            flex: 1,
            backgroundColor: '#ffffff',
            border: '2px solid #e0e0e0',
            borderRadius: '8px',
            padding: '20px',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            display: 'flex',
            flexDirection: 'column',
            height: '400px',
          }}>
            <div style={{
              fontSize: '16px',
              fontWeight: 'bold',
              color: '#333',
              marginBottom: '15px',
            }}>
              채팅방 선택
            </div>
            <div style={{
              display: 'flex',
              gap: '10px',
              alignItems: 'center',
            }}>
              <select
                value={selectedChatRoom}
                onChange={(e) => setSelectedChatRoom(e.target.value)}
                style={{
                  flex: 1,
                  padding: '10px 15px',
                  border: '1px solid #d0d0d0',
                  borderRadius: '6px',
                  fontSize: '14px',
                  backgroundColor: '#ffffff',
                  cursor: 'pointer',
                  outline: 'none',
                }}
              >
                {chatRooms.map((room) => (
                  <option key={room} value={room}>
                    {room}
                  </option>
                ))}
              </select>
              <button
                onClick={handleCreateChatRoom}
                style={{
                  padding: '10px 16px',
                  backgroundColor: '#28a745',
                  color: '#ffffff',
                  border: 'none',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  fontSize: '14px',
                  fontWeight: 'bold',
                  whiteSpace: 'nowrap',
                }}
                onMouseOver={(e) => {
                  e.currentTarget.style.backgroundColor = '#218838';
                }}
                onMouseOut={(e) => {
                  e.currentTarget.style.backgroundColor = '#28a745';
                }}
              >
                생성
              </button>
            </div>
          </div>
        </div>

        {/* 서브 채팅방 3x3 그리드 */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(3, 1fr)',
          gap: '20px',
        }}>
          {Array.from({ length: 9 }, (_, i) => (
            <ChatRoom key={i} title={`서브 채팅방 ${i + 1}`} nickname={`사용자${i + 1}`} />
          ))}
        </div>
      </div>
    </div>
  );
}

