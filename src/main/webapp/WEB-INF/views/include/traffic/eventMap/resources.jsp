<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

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
	    overflow-y: auto;
	    padding: 10px;
	    border-right: 1px solid #ccc;
	    z-index: 10;
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

	  .map {
	    width: 100%;
	    height: 100%;
	  }
	</style>





</body>
</html>