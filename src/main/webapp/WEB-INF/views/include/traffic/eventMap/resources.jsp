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


  #eventList {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  #eventList li {
    padding: 6px;
    border-bottom: 1px solid #ddd;
    cursor: pointer;
  }

  #eventList li:hover {
    background-color: #eee;
  }

  .active-list-item {
    background-color: #ffeb3b33;
    border-left: 4px solid #ffc107;
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
