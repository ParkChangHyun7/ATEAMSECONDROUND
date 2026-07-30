<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<style>
  .cpabout {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    position: relative;
    overflow: hidden;
  }

  .cpabout::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
      radial-gradient(circle at 40% 40%, rgba(120, 219, 255, 0.2) 0%, transparent 50%);
    animation: backgroundFloat 20s ease-in-out infinite;
  }

  @keyframes backgroundFloat {
    0%, 100% { transform: translateY(0px) rotate(0deg); }
    50% { transform: translateY(-20px) rotate(1deg); }
  }

  .cpabout .inner {
    width: 1200px;
    margin: 0 auto;
    padding-top: 100px;
    padding-bottom: 50px;
    position: relative;
    z-index: 1;
  }

  .cpabout .page-title {
    font-size: 42px;
    text-align: center;
    margin-bottom: 60px;
    font-weight: 700;
    color: white;
    text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    animation: titleGlow 3s ease-in-out infinite alternate;
    position: relative;
  }

  .cpabout .page-title::after {
    content: '';
    position: absolute;
    bottom: -15px;
    left: 50%;
    transform: translateX(-50%);
    width: 100px;
    height: 4px;
    background: linear-gradient(90deg, #ff6b6b, #4ecdc4, #45b7d1);
    border-radius: 2px;
    animation: underlineGlow 2s ease-in-out infinite alternate;
  }

  @keyframes titleGlow {
    0% { text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3); }
    100% { text-shadow: 0 4px 30px rgba(255, 255, 255, 0.4), 0 0 40px rgba(255, 255, 255, 0.2); }
  }

  @keyframes underlineGlow {
    0% { box-shadow: 0 0 10px rgba(255, 107, 107, 0.5); }
    100% { box-shadow: 0 0 20px rgba(69, 183, 209, 0.8), 0 0 30px rgba(78, 205, 196, 0.6); }
  }

  .cpabout .tab-nav {
    display: flex;
    justify-content: center;
    gap: 15px;
    margin-bottom: 40px;
    flex-wrap: wrap;
  }

  .cpabout .tab-nav-item {
    padding: 15px 25px;
    background: rgba(255, 255, 255, 0.1);
    border: 2px solid rgba(255, 255, 255, 0.2);
    border-radius: 25px;
    cursor: pointer;
    font-size: 16px;
    font-weight: 600;
    color: white;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    backdrop-filter: blur(10px);
    position: relative;
    overflow: hidden;
  }

  .cpabout .tab-nav-item::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    transition: left 0.5s;
  }

  .cpabout .tab-nav-item:hover {
    transform: translateY(-5px) scale(1.05);
    background: rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.4);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  }

  .cpabout .tab-nav-item:hover::before {
    left: 100%;
  }

  .cpabout .tab-nav-item.is-active {
    background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
    border-color: rgba(255, 255, 255, 0.6);
    transform: translateY(-3px);
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
    animation: activeTabPulse 2s ease-in-out infinite;
  }

  @keyframes activeTabPulse {
    0%, 100% { box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3); }
    50% { box-shadow: 0 15px 35px rgba(255, 107, 107, 0.4), 0 0 30px rgba(78, 205, 196, 0.3); }
  }

  .cpabout .tab-content {
    padding: 40px;
    display: none;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(20px);
    animation: contentSlideIn 0.6s ease-out;
    position: relative;
    overflow: hidden;
  }

  .cpabout .tab-content::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #ff6b6b, #4ecdc4, #45b7d1);
    animation: topBorderFlow 3s linear infinite;
  }

  @keyframes topBorderFlow {
    0% { transform: translateX(-100%); }
    100% { transform: translateX(100%); }
  }

  @keyframes contentSlideIn {
    0% { 
      opacity: 0; 
      transform: translateY(30px) scale(0.95); 
    }
    100% { 
      opacity: 1; 
      transform: translateY(0) scale(1); 
    }
  }

  .cpabout .tab-content.is-active {
    display: block;
  }

  .cpabout .tab-content-title {
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 30px;
    padding-bottom: 15px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    position: relative;
  }

  .cpabout .tab-content-title::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 60px;
    height: 3px;
    background: linear-gradient(90deg, #ff6b6b, #4ecdc4);
    border-radius: 2px;
    animation: titleUnderlineExpand 1s ease-out;
  }

  @keyframes titleUnderlineExpand {
    0% { width: 0; }
    100% { width: 60px; }
  }

  .cpabout .tab-content-subtitle {
    font-size: 22px;
    font-weight: 600;
    margin-bottom: 25px;
    color: #2c3e50;
  }

  .cpabout .info-card {
    display: flex;
    gap: 25px;
    align-items: center;
    margin-bottom: 30px;
    padding: 30px;
    border: none;
    border-radius: 15px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(240, 248, 255, 0.9));
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    transition: all 0.4s ease;
    position: relative;
    overflow: hidden;
  }

  .cpabout .info-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    transition: left 0.6s;
  }

  .cpabout .info-card:hover {
    transform: translateY(-8px) scale(1.02);
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }

  .cpabout .info-card:hover::before {
    left: 100%;
  }

  .cpabout .info-card-icon {
    flex-shrink: 0;
    width: 120px;
    height: 120px;
    border-radius: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #667eea, #764ba2);
    box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
    transition: all 0.4s ease;
    animation: iconFloat 3s ease-in-out infinite;
  }

  @keyframes iconFloat {
    0%, 100% { transform: translateY(0px) rotate(0deg); }
    50% { transform: translateY(-10px) rotate(5deg); }
  }

  .cpabout .info-card:hover .info-card-icon {
    transform: scale(1.1) rotate(10deg);
    box-shadow: 0 15px 35px rgba(102, 126, 234, 0.4);
  }

  .cpabout .info-card-icon svg {
    fill: white;
    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
  }

  .cpabout .info-card-body {
    flex-grow: 1;
  }

  .cpabout .info-card-title {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 15px;
    background: linear-gradient(135deg, #2c3e50, #3498db);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .cpabout .info-card-description {
    font-size: 16px;
    line-height: 1.8;
    padding-left: 0;
    color: #555;
  }

  .cpabout .info-card-description li {
    list-style-position: outside;
    margin-left: 1.2em;
    padding: 8px 0;
    transition: all 0.3s ease;
    position: relative;
  }

  .cpabout .info-card-description li::before {
    content: '✨';
    position: absolute;
    left: -1.5em;
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  .cpabout .info-card:hover .info-card-description li::before {
    opacity: 1;
  }

  .cpabout .info-card-description .highlight {
    background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    font-weight: 700;
    position: relative;
  }

  .cpabout .org-overview {
    margin: 0 auto 40px auto;
    max-width: 800px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(248, 250, 252, 0.95));
    border-radius: 20px;
    padding: 40px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(20px);
    position: relative;
    overflow: hidden;
    animation: overviewFloat 6s ease-in-out infinite;
  }

  .cpabout .org-overview::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #ff6b6b, #4ecdc4, #45b7d1);
    animation: topBorderFlow 3s linear infinite;
  }

  @keyframes overviewFloat {
    0%, 100% { transform: translateY(0px); }
    50% { transform: translateY(-5px); }
  }

  .cpabout .org-overview-title {
    font-size: 28px;
    font-weight: 700;
    text-align: center;
    margin-bottom: 25px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .cpabout .org-overview-content p {
    font-size: 18px;
    line-height: 1.8;
    color: #555;
    text-align: center;
    margin-bottom: 30px;
  }

  .cpabout .org-stats {
    display: flex;
    justify-content: space-around;
    gap: 20px;
  }

  .cpabout .stat-item {
    text-align: center;
    padding: 20px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    border-radius: 15px;
    transition: all 0.3s ease;
    flex: 1;
  }

  .cpabout .stat-item:hover {
    transform: translateY(-5px) scale(1.05);
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
  }

  .cpabout .stat-number {
    display: block;
    font-size: 32px;
    font-weight: 700;
    background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin-bottom: 8px;
  }

  .cpabout .stat-label {
    font-size: 14px;
    color: #667eea;
    font-weight: 600;
  }

  .cpabout .org-chart-details {
    display: none !important;
  }

  .cpabout .org-chart {
    display: flex;
    gap: 25px;
    justify-content: space-between;
    margin-bottom: 40px;
  }

  .cpabout .org-chart-item {
    flex: 1;
    border: none;
    border-radius: 15px;
    overflow: hidden;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(248, 250, 252, 0.9));
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    position: relative;
  }

  .cpabout .org-chart-item::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #ff6b6b, #4ecdc4, #45b7d1);
    transform: scaleX(0);
    transition: transform 0.4s ease;
  }

  .cpabout .org-chart-item:hover {
    transform: translateY(-10px) scale(1.03);
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }

  .cpabout .org-chart-item:hover::before {
    transform: scaleX(1);
  }

  .cpabout .org-chart-title {
    display: block;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    font-size: 20px;
    font-weight: 700;
    text-align: center;
    padding: 20px;
    margin-bottom: 0;
    border: none;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
  }

  .cpabout .org-chart-title::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    transition: left 0.5s;
  }

  .cpabout .org-chart-item:hover .org-chart-title::before {
    left: 100%;
  }

  .cpabout .org-chart-duties {
    padding: 25px;
  }

  .cpabout .org-chart-duties li {
    font-size: 16px;
    padding: 10px 0;
    list-style-type: none;
    margin-left: 0;
    position: relative;
    color: #555;
    transition: all 0.3s ease;
  }

  .cpabout .org-chart-duties li::before {
    content: '▶';
    color: #667eea;
    font-weight: bold;
    margin-right: 10px;
    transition: all 0.3s ease;
  }

  .cpabout .org-chart-item:hover .org-chart-duties li::before {
    color: #ff6b6b;
    transform: translateX(5px);
  }

  .cpabout .org-chart-duties .indent {
    margin-left: 1.5em;
    font-size: 0.9em;
    color: #777;
    font-style: italic;
  }

  .cpabout .org-chart-details {
    display: none !important;
  }

  .cpabout .data-table {
    width: 100%;
    margin: 30px 0 50px 0;
    max-width: 100%;
    border-collapse: collapse;
    border: none;
    font-size: 16px;
    border-radius: 15px;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    background: white;
  }

  .cpabout .data-table th,
  .cpabout .data-table td {
    border: 1px solid rgba(0, 0, 0, 0.05);
    padding: 18px 20px;
    text-align: left;
    vertical-align: top;
    transition: all 0.3s ease;
  }

  .cpabout .data-table thead th {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    font-size: 18px;
    font-weight: 700;
    text-align: center;
    border: none;
    position: relative;
  }

  .cpabout .data-table tbody tr {
    transition: all 0.3s ease;
  }

  .cpabout .data-table tbody tr:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
    transform: scale(1.01);
  }

  .cpabout .data-table tbody tr:nth-child(even) {
    background: rgba(248, 250, 252, 0.5);
  }

  .cpabout .data-table-roles th:nth-child(1) { width: 25%; }
  .cpabout .data-table-roles th:nth-child(2) { width: 25%; }
  .cpabout .data-table-roles th:nth-child(3) { width: 50%; }

  .cpabout .data-table-roles td {
    text-align: center;
  }

  .cpabout .data-table-roles td:last-child {
    text-align: left;
  }

  .cpabout .data-table-roles ul {
    padding-left: 0;
    margin: 0;
  }

  .cpabout .data-table-roles li {
    list-style: none;
    margin-left: 0;
    padding: 8px 0;
    font-size: 16px;
    text-align: left;
    position: relative;
    transition: all 0.3s ease;
  }

  .cpabout .data-table-roles li::before {
    content: '🔹';
    margin-right: 8px;
  }

  .cpabout .data-table-status th:nth-child(1) { width: 20%; }
  .cpabout .data-table-status th:nth-child(2) { width: 50%; }
  .cpabout .data-table-status th:nth-child(3) { width: 30%; text-align: center; }

  .cpabout .data-table-status td {
    vertical-align: middle;
  }

  .cpabout .data-table-status .category-title {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    font-weight: 700;
    text-align: center;
    color: #667eea;
  }

  .cpabout .data-table-status .cell-list {
    padding: 0;
    margin: 0;
  }

  .cpabout .data-table-status .cell-list li {
    font-size: 16px;
    border-bottom: 1px dashed rgba(102, 126, 234, 0.2);
    padding: 10px 0;
    list-style: none;
    margin: 0;
    transition: all 0.3s ease;
  }

  .cpabout .data-table-status .cell-list li:hover {
    color: #667eea;
    padding-left: 10px;
  }

  .cpabout .data-table-status .cell-list li:last-child {
    border-bottom: none;
  }

  .cpabout .data-table-status td:last-child {
    text-align: center;
    font-weight: 600;
    color: #667eea;
  }

  .cpabout .data-table-status tfoot td {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    font-weight: 700;
    text-align: center;
    font-size: 18px;
  }

  .cpabout .map-container {
    margin: 0 auto 40px auto;
    width: 950px;
    max-width: 100%;
    border-radius: 15px;
    overflow: hidden;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
    transition: all 0.4s ease;
  }

  .cpabout .map-container:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }

  .cpabout .map-container iframe {
    display: block;
    width: 100%;
    border: none;
    transition: all 0.3s ease;
  }

  .cpabout .data-table-location th:nth-child(1) {
    width: 25%;
    text-align: center;
  }

  .cpabout .data-table-location th:nth-child(2) {
    width: 75%;
  }

  .cpabout .data-table-location .label-cell {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    font-weight: 700;
    text-align: center;
    color: #667eea;
  }

  .cpabout .data-table-location .value-cell {
    padding-left: 25px;
    color: #555;
  }

  .cpabout .data-table-location ul {
    margin: 0;
    padding-left: 0;
  }

  .cpabout .data-table-location li {
    list-style: none;
    padding: 5px 0;
    transition: all 0.3s ease;
  }

  .cpabout .data-table-location li:hover {
    color: #667eea;
    padding-left: 10px;
  }

  .cpabout .data-table-location .indent {
    list-style: none;
    margin-left: 1.5em;
    padding-top: 5px;
    position: relative;
  }

  .cpabout .data-table-location .indent::before {
    content: '→';
    color: #667eea;
    margin-right: 8px;
    font-weight: bold;
  }

  .cpabout .org-chart-details {
    padding: 25px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
    border-top: 1px solid rgba(102, 126, 234, 0.2);
    display: none;
    font-size: 15px;
    line-height: 1.8;
    color: #555;
  }

  .cpabout .org-chart-details p {
    margin-bottom: 12px;
    transition: all 0.3s ease;
  }

  .cpabout .org-chart-details p:last-child {
    margin-bottom: 0;
  }

  .cpabout .org-chart-details strong {
    color: #667eea;
    font-weight: 700;
  }

  .cpabout .org-chart-item.is-expanded .org-chart-details {
    display: block;
    animation: detailsSlideDown 0.4s ease-out;
  }

  @keyframes detailsSlideDown {
    0% { 
      opacity: 0; 
      max-height: 0; 
      transform: translateY(-10px); 
    }
    100% { 
      opacity: 1; 
      max-height: 200px; 
      transform: translateY(0); 
    }
  }

  /* 반응형 디자인 */
  @media (max-width: 1024px) {
    .cpabout .inner {
      width: 95%;
      padding: 80px 20px 40px;
    }
    
    .cpabout .org-chart {
      flex-direction: column;
      gap: 20px;
    }
    
    .cpabout .info-card {
      flex-direction: column;
      text-align: center;
    }
    
    .cpabout .info-card-icon {
      width: 100px;
      height: 100px;
    }
  }

  @media (max-width: 768px) {
    .cpabout .page-title {
      font-size: 32px;
    }
    
    .cpabout .tab-nav {
      flex-direction: column;
      align-items: center;
    }
    
    .cpabout .tab-nav-item {
      width: 80%;
      text-align: center;
    }
    
    .cpabout .tab-content {
      padding: 25px;
    }
  }
</style>

<div class="cpabout">
  <main class="inner">
    <h1 class="page-title">서울교통정보센터 소개</h1>

    <nav class="tab-nav">
      <button type="button" class="tab-nav-item is-active">ITS 사업목표</button>
      <button type="button" class="tab-nav-item goal">운영조직</button>
      <button type="button" class="tab-nav-item role">정보센터 역할</button>
      <button type="button" class="tab-nav-item status">ITS 구축현황</button>
      <button type="button" class="tab-nav-item">오시는 길</button>
    </nav>

    <div class="tab-content-container">

      <%-- Tab 1: ITS 사업목표 --%>
      <section class="tab-content is-active" id="tab-its-goal">
        <h2 class="tab-content-title">ITS 사업목적</h2>
        <p class="tab-content-subtitle"><strong>통합도로 교통체계(IRTS)</strong></p>

        <article class="info-card">
          <div class="info-card-icon"><svg xmlns="http://www.w3.org/2000/svg" height="100px"
              viewBox="0 -960 960 960" width="100px" fill="#333">
              <path
                d="M480-234q24 0 41-17t17-41q0-24-17-41t-41-17q-24 0-41 17t-17 41q0 24 17 41t41 17Zm0-188q24 0 41-17t17-41q0-24-17-41t-41-17q-24 0-41 17t-17 41q0 24 17 41t41 17Zm0-188q24 0 41-17t17-41q0-24-17-41t-41-17q-24 0-41 17t-17 41q0 24 17 41t41 17ZM275-347v-80q-51-11-83-47.5T160-554h115v-77q-51-11-83-47t-32-79h115v-24q0-26 17-42.5t43-16.5h290q26 0 43 16.5t17 42.5v24h115q0 43-32 79t-83 47v77h115q0 43-32 79.5T685-427v80h115q0 45-32 80t-83 46v41q0 26-17 43t-43 17H335q-26 0-43-17t-17-43v-41q-51-11-83-46t-32-80h115Zm60 167h290v-600H335v600Zm0 0v-600 600Z" />
            </svg></div>
          <div class="info-card-body">
            <h3 class="info-card-title">교통관리 최적화</h3>
            <ul class="info-card-description">
              <li>교통정체, 돌발 등 소통상태를 정확히 파악</li>
              <li>교통량 증가, 억제 및 분산 유도를 통해 <span class="highlight">최적의 교통상태 유지</span></li>
            </ul>
          </div>
        </article>

        <article class="info-card">
          <div class="info-card-icon"><svg xmlns="http://www.w3.org/2000/svg" height="100px"
              viewBox="0 -960 960 960" width="100px" fill="#333">
              <path
                d="M355-120q-65 0-110-45.53T200-275v-349q-35-13-57.5-41.26-22.5-28.27-22.5-64.41Q120-776 152.5-808t78-32q45.5 0 77.5 32.14t32 78.05q0 35.81-22.5 64.31T260-624v349q0 39.19 27.5 67.09Q315-180 355.5-180t67.5-27.91q27-27.9 27-67.09v-410q0-65 45-110t110-45q65 0 110 45t45 110v349q35 13 57.5 41.36Q840-266.27 840-230q0 45-32.08 77.5Q775.83-120 730-120q-45 0-77.5-32.5T620-230q0-36.3 22.5-65.15Q665-324 700-336v-349q0-40-27.5-67.5T605-780q-40 0-67.5 27.5T510-685v410q0 63.94-45 109.47T355-120ZM230.5-680q20.5 0 35-15t14.5-35.5q0-20.5-14.37-35Q251.25-780 230-780q-20 0-35 14.37-15 14.38-15 35.63 0 20 15 35t35.5 15Zm500 500q20.5 0 35-15t14.5-35.5q0-20.5-14.37-35Q751.25-280 730-280q-20 0-35 14.37-15 14.38-15 35.63 0 20 15 35t35.5 15ZM230-730Zm500 500Z" />
            </svg></div>
          <div class="info-card-body">
            <h3 class="info-card-title">도로관리 효율화</h3>
            <ul class="info-card-description">
              <li>도로교통 주행 환경의 상시적인 모니터링 체계</li>
              <li>도로, 하천, 교량 등 재난 상황 발생 시 신속한 감시체계 구축</li>
              <li>정확한 상황 파악, 관계기관에 <span class="highlight">신속한 연락업무 수행</span></li>
              <li>도로감시 체제로 2차 사고 및 사고 확대 예방</li>
            </ul>
          </div>
        </article>

        <article class="info-card">
          <div class="info-card-icon"><svg xmlns="http://www.w3.org/2000/svg" height="100px"
              viewBox="0 -960 960 960" width="100px" fill="#333">
              <path
                d="M450-80v-190H230L120-380l110-110h220v-90H160v-220h290v-80h60v80h220l110 110-110 110H510v90h290v220H510v190h-60ZM220-640h485l50-50-50-50H220v100Zm35 310h485v-100H255l-50 50 50 50Zm-35-310v-100 100Zm520 310v-100 100Z" />
            </svg></div>
          <div class="info-card-body">
            <h3 class="info-card-title">다양한 이용자 서비스</h3>
            <ul class="info-card-description">
              <li>다양하고 편리한 도로주행 환경 제공</li>
              <li>다양한 경로 선택, 여행시간 등 정보 제공</li>
              <li>언제 어디서나 편리한 정보이용 환경 제공</li>
            </ul>
          </div>
        </article>
      </section>

      <%-- Tab 2: 운영조직 --%>
      <section class="tab-content" id="tab-organization">
        <h2 class="tab-content-title">운영조직</h2>
        <div class="org-overview">
          <h3 class="org-overview-title">서울지방국토관리청 도로교통정보센터</h3>
          <div class="org-overview-content">
            <p>국도 교통정보 수집·가공·제공을 통한 교통서비스 향상과 도로관리 효율화를 위해 설립된 전문기관입니다.</p>
            <div class="org-stats">
              <div class="stat-item">
                <span class="stat-number">24</span>
                <span class="stat-label">시간 운영</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">3,625</span>
                <span class="stat-label">개 시설</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">670</span>
                <span class="stat-label">개 CCTV</span>
              </div>
            </div>
          </div>
        </div>

        <div class="org-chart">
          <article class="org-chart-item">
            <h3 class="org-chart-title">사업관리</h3>
            <ul class="org-chart-duties">
              <li>국도 ITS 사업관리(구축, 운영)</li>
              <li>예산집행</li>
              <li>교통정보 분석</li>
              <li>실시설계</li>
              <li>기본계획 수립</li>
            </ul>
            <div class="org-chart-details">
              <p><strong>주요 담당자:</strong> 홍길동 팀장</p>
              <p>사업 계획 수립 및 예산 확보, 전체 사업 진행 상황 점검 및 관리 감독을 책임집니다.</p>
            </div>
          </article>

          <article class="org-chart-item">
            <h3 class="org-chart-title">운영</h3>
            <ul class="org-chart-duties">
              <li>센터 상황실 운영 및 시스템 관리</li>
              <li>교통품질관리</li>
              <li>돌발상황 대응 및 장애 처리</li>
              <li>유지관리 감독 및 기술지원</li>
            </ul>
            <div class="org-chart-details">
              <p><strong>주요 담당자:</strong> 김철수 파트장</p>
              <p>24시간 상황실 모니터링, 시스템 안정성 확보, 실시간 교통 정보 품질 유지 및 돌발 상황 발생 시 초기 대응을 담당합니다.</p>
            </div>
          </article>

          <article class="org-chart-item">
            <h3 class="org-chart-title">유지관리</h3>
            <ul class="org-chart-duties">
              <li>센터시스템 유지 관리<br />
                <p class="indent">(서버, 네트워크, 상황판 등)</p>
              </li>
              <li>현장시스템 유지 관리<br />
                <p class="indent">(현장장비, 통신장비 등)</p>
              </li>
              <li>시스템 가동률 향상</li>
              <li>교통데이터 및 일지관리</li>
            </ul>
            <div class="org-chart-details">
              <p><strong>주요 담당자:</strong> 박영희 주임</p>
              <p>정기적인 시스템 점검 및 예방 정비, 현장 장비 장애 발생 시 신속한 복구, 시스템 성능 개선 작업을 수행합니다.</p>
            </div>
          </article>
        </div>
      </section>

      <%-- Tab 3: 정보센터 역할 --%>
      <section class="tab-content" id="tab-center-role">
        <h2 class="tab-content-title">정보센터 역할</h2>
        <table class="data-table data-table-roles">
          <thead>
            <tr>
              <th>국도교통정보센터</th>
              <th>관련기관</th>
              <th>주요역할</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>국도ITS센터</td>
              <td>서울지방국토관리청</td>
              <td>
                <ul>
                  <li>국도 ITS 사업추진 및 센터 운영</li>
                  <li>관할구역내 지방자치단체 등 유관기관과의 정보 연계</li>
                  <li>ITS를 활용한 실시간 교통관리 및 도로 관리 업무 추진</li>
                  <li>돌발 상황 감시 및 대응 체계 구축</li>
                  <li>정보수집 / 가공 / 제공 체계 구축</li>
                  <li>현장 설비와 시설물의 예방 정비 및 유지 보수</li>
                  <li>국토관리사무소 업무 지위 및 정보 공유</li>
                </ul>
              </td>
            </tr>
          </tbody>
        </table>
      </section>

      <%-- Tab 4: ITS 구축현황 --%>
      <section class="tab-content" id="tab-its-status">
        <%-- TODO: sectionTitle이 이전 탭과 동일함, 수정 필요시 변경 --%>
        <h2 class="tab-content-title">ITS 구축현황</h2>
        <p class="tab-content-subtitle"><strong>서울청 ITS 시설 현황</strong></p>
        <table class="data-table data-table-status">
          <thead>
            <tr>
              <th>구분</th>
              <th>장비명</th>
              <th>수량</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="category-title">시설물</td>
              <td>
                <ul class="cell-list">
                  <li>CCTV(스마트CCTV)</li>
                  <li>WEB CAMERA</li>
                  <li>도로 전광 표지지</li>
                  <li>VDS(차량검지기)</li>
                  <li>DSRC</li>
                  <li>RWIS</li>
                  <li>AVI(챠량번호인식)</li>
                  <li>레이더 돌발검지기</li>
                  <li>RTSC(실시간교통제어)</li>
                </ul>
              </td>
              <td>
                <ul class="cell-list">
                  <li>670(519)</li>
                  <li>802</li>
                  <li>369</li>
                  <li>339</li>
                  <li>253</li>
                  <li>-</li>
                  <li>-</li>
                  <li>-</li>
                  <li>-</li>
                </ul>
              </td>
            </tr>
            <tr>
              <td class="category-title">결빙취약구간</td>
              <td>
                <ul class="cell-list">
                  <li>VSL</li>
                  <li>기상센터(온습도)</li>
                  <li>안내표지판</li>
                </ul>
              </td>
              <td>
                <ul class="cell-list">
                  <li>429</li>
                  <li>-</li>
                  <li>639</li>
                </ul>
              </td>
            </tr>
            <tr>
              <td class="category-title">민간</td>
              <td>
                <ul class="cell-list">
                  <li>IoT 스마트안전</li>
                  <li>주행소리AI분석</li>
                </ul>
              </td>
              <td>
                <ul class="cell-list">
                  <li>22</li>
                  <li>2</li>
                </ul>
              </td>
            </tr>
            <tr>
              <td class="category-title">기타</td>
              <td>
                <ul class="cell-list">
                  <li>교통류 스마트제어</li>
                </ul>
              </td>
              <td>22</td>
            </tr>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="2">계</td>
              <td>3,625</td>
            </tr>
          </tfoot>
        </table>
      </section>

      <%-- Tab 5: 오시는 길 --%>
      <section class="tab-content" id="tab-location">
        <h2 class="tab-content-title">오시는 길</h2>
        <div class="map-container">
          <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3168.3997873151243!2d126.98404701195788!3d37.42765937195843!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x357ca28e817cf457%3A0x9339cfc6b2b089e8!2z7ISc7Jq47KeA67Cp6rWt7Yag6rSA66as7LKt!5e0!3m2!1sko!2skr!4v1745420757790!5m2!1sko!2skr"
            height="450" <%-- Adjusted height --%>
            allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade">
          </iframe>
        </div>
        <table class="data-table data-table-location">
          <tbody>
            <tr>
              <th class="label-cell">주소</th>
              <td class="value-cell">경기도 과천시 관문로 47(중앙동) 정부과천청사 2동 1층(13809)</td>
            </tr>
            <tr>
              <th class="label-cell">대표전화</th>
              <td class="value-cell">Tel) 02-756-0975</td>
            </tr>
            <tr>
              <th class="label-cell">교통편 안내</th>
              <td class="value-cell">
                <ul>
                  <li>지하철 이용 시</li>
                  <li class="indent">4호선 정부과천 청사역 하자 7,8번 출구</li>
                  <li>버스 이용 시</li>
                  <li class="indent">1-1, 1-6, 9, 9-2, 11, 11-2, 97-2, 888</li>
                  <li>좌석/공항버스 이용 시</li>
                  <li class="indent">550, 552, 797, 908, 918</li>
                </ul>
              </td>
            </tr>
            <tr>
              <th class="label-cell">담당자</th>
              <td class="value-cell">OOO 주무관 Tel) 02-2110-6822</td>
            </tr>
          </tbody>
        </table>
      </section>
    </div>
  </main>
</div>

<script>
  const aboutContainer = document.querySelector(".cpabout");

  if (aboutContainer) {
    const tabs = aboutContainer.querySelectorAll(".tab-nav-item");
    const contents = aboutContainer.querySelectorAll(".tab-content");

    if (tabs.length > 0 && tabs.length === contents.length) {
      tabs.forEach((tab, index) => {
        tab.addEventListener("click", () => {
          if (!tab.closest('.cpabout')) return;

          const currentTabs = aboutContainer.querySelectorAll('.tab-nav-item');
          const currentContents = aboutContainer.querySelectorAll('.tab-content');

          currentTabs.forEach(t => t.classList.remove("is-active"));
          currentContents.forEach(c => c.classList.remove("is-active"));

          tab.classList.add("is-active");

          if (contents[index] && contents[index].closest('.cpabout')) {
            contents[index].classList.add("is-active");
          }
        });
      });
    } else {
      console.warn("About page tabs/contents not found or mismatched.");
    }

    const orgChartItems = aboutContainer.querySelectorAll(".org-chart-item");

    if (orgChartItems.length > 0) {
        orgChartItems.forEach(item => {
            const title = item.querySelector('.org-chart-title');
            const details = item.querySelector('.org-chart-details');

            if (title && details) {
                title.addEventListener('click', (event) => {
                    event.stopPropagation();

                    const isExpanded = item.classList.contains('is-expanded');

                    orgChartItems.forEach(otherItem => {
                        if (otherItem !== item) {
                            otherItem.classList.remove('is-expanded');
                            const otherDetails = otherItem.querySelector('.org-chart-details');
                            if (otherDetails) otherDetails.style.display = 'none';
                        }
                    });

                    if (isExpanded) {
                        item.classList.remove('is-expanded');
                        details.style.display = 'none';
                    } else {
                        item.classList.add('is-expanded');
                        details.style.display = 'block';
                    }
                });

                item.addEventListener('click', (event) => {
                    if (event.target !== title && !title.contains(event.target)) {
                         title.click();
                    }
                });
            }
        });
    } else {
        console.warn("Organization chart items not found.");
    }

  } else {
      console.warn("About container .cpabout not found.");
  }
</script>