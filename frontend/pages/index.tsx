import React from 'react';

export default function Home() {
  return (
    <div style={{ 
      display: 'flex', 
      flexDirection: 'column', 
      alignItems: 'center', 
      justifyContent: 'center', 
      minHeight: '100vh',
      fontFamily: 'system-ui, -apple-system, sans-serif',
      padding: '2rem'
    }}>
      <h1 style={{ 
        fontSize: '2.5rem', 
        marginBottom: '1rem',
        color: '#333'
      }}>
        Welcome to Live Platform Lab
      </h1>
      <p style={{ 
        fontSize: '1.2rem', 
        color: '#666',
        textAlign: 'center'
      }}>
        Index 페이지가 정상적으로 표시되고 있습니다.
      </p>
    </div>
  );
}

