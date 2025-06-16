<style>
  .event-container {
    position: relative;
    height: 100vh;
  }

  .sidebar {
    position: absolute;
    top: 0;
    left: 0;
    width: 300px;
    height: 100%;
    background-color: #f5f5f5;
    padding: 10px;
    border-right: 1px solid #ccc;
    box-sizing: border-box;
    z-index: 10;
    display: flex;
    flex-direction: column;
  }

  .sidebar h3 {
    margin-bottom: 10px;
    font-size: 18px;
    border-bottom: 1px solid #aaa;
    padding-bottom: 5px;
  }

  #filterButtons {
    display: flex;
    flex-wrap: wrap;
    gap: 5px;
    margin-bottom: 10px;
  }

  .filter-btn {
    flex: 1 0 30%;
    padding: 5px;
    font-size: 13px;
    border: 1px solid #888;
    background-color: #fff;
    cursor: pointer;
  }

  .filter-btn.full {
    flex: 0 0 100%;
  }

  .filter-btn.active {
    background-color: #ff9800;
    color: white;
    font-weight: bold;
  }

#eventListWrapper {
  height: 65vh; 
  overflow-y: auto;
  position: relative;
}


  .table-container {
    width: 100%;
    overflow-x: auto;
  }

  .event-table {
    width: 100%;
    border-collapse: collapse;
    font-size: 12px;
  }

  .event-table thead {
    background-color: #f8f9fa;
    position: sticky;
    top: 0;
    z-index: 5;
  }

  .event-table th {
    padding: 8px 6px;
    border: 1px solid #dee2e6;
    font-weight: bold;
    text-align: center;
    background-color: #e9ecef;
    font-size: 11px;
  }

  .event-table th:nth-child(1) { width: 25%; } /* 구분 */
  .event-table th:nth-child(2) { width: 35%; } /* 도로명 */
  .event-table th:nth-child(3) { width: 40%; } /* 상황 */

  .event-table tbody {
    max-height: calc(65vh - 40px);
  }

  .event-table td {
    padding: 6px 4px;
    border: 1px solid #dee2e6;
    vertical-align: top;
    word-wrap: break-word;
    font-size: 11px;
    line-height: 1.3;
  }

  .event-row {
    cursor: pointer;
    transition: background-color 0.2s ease;
  }

  .event-row:hover {
    background-color: #f8f9fa;
  }

  .event-category {
    text-align: center;
    font-weight: bold;
  }

  .event-road {
    font-weight: 500;
  }

  .event-message {
    font-size: 10px;
    color: #666;
  }

  .active-list-item {
    background-color: #fff3cd !important;
    border-left: 4px solid #ffc107 !important;
  }

  .active-list-item td {
    background-color: #fff3cd;
  }

  #scrollToTopBtn {
    position: sticky;
    bottom: 0;
    width: 100%;
    display: none;
    padding: 8px 0;
    background-color: #eee;
    border: none;
    text-align: center;
    font-weight: bold;
    cursor: pointer;
    z-index: 999;
  }

  .map {
    width: 100%;
    height: 100%;
  }
</style>