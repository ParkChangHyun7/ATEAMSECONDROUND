<style>
  .cctv-container {
    display: flex;
    height: 100vh;
  }

  .sidebar {
    width: 300px;
    background: #f5f5f5;
    overflow-y: auto;
    padding: 10px;
    border-right: 1px solid #ccc;
  }

  .sidebar h3 {
    margin: 0 0 10px;
    font-size: 18px;
    border-bottom: 1px solid #aaa;
    padding-bottom: 5px;
  }

  #cctvList {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  #cctvList li {
    padding: 8px 6px;
    cursor: pointer;
    border-bottom: 1px solid #ddd;
  }

  #cctvList li:hover {
    background-color: #eee;
  }

  .map {
    flex-grow: 1;
  }
  
    #filterButtons {
    display: flex;
    gap: 5px;
  }

  .filter-btn {
    flex: 1;
    padding: 5px;
    border: 1px solid #999;
    background-color: #fff;
    cursor: pointer;
    font-size: 13px;
  }

  .filter-btn.active {
    background-color: #4a90e2;
    color: white;
    font-weight: bold;
  }
  
  
  
</style>
