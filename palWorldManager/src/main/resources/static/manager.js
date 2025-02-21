

var login = function() {
	var data = $("#login").serialize();
	$.ajax({
		url: "/manager/login",
		method: "post",
		data: data,
		success: function(res) {
			if (res) {
				alert("成功");

			} else {
				alert("密碼錯誤");
			}
		}
	})
}

var start = function() {
	$.ajax({
		url: "/manager/run",
		method: "post",
		success: function(res) {
			if (res) {
				alert("成功,正在開啟server");
			} else {
				alert("錯誤");
			}
		}
	})
}

var shutdown = function() {
	$.ajax({
		url: "/manager/shutdown",
		method: "post",
		success: function(res) {
			if (res) {
				alert("成功,正在關閉server");
			} else {
				alert("錯誤");
			}
		}
	})
}

var updateVersion = function() {
	var check = confirm("請先關閉server再執行版本更新");
	if (check) {
		$.ajax({
			url: "/manager/updateVersion",
			method: "post",
			success: function(res) {
				if (res) {
					alert("成功,正在更新版本");
				} else {
					alert("錯誤");
				}
			}
		})
	}
}

var getFileList = function() {
	$.ajax({
		url: "/manager/getFileList",
		method: "post",
		success: function(res) {
			var object = JSON.parse(res);
			debugger;
			var fileList = document.getElementById("fileList");
			fileList.innerHTML = "";
			for (i of object) {
				var option = document.createElement("option");
				option.text = i['value'];
				option.value = i['value'];
				fileList.add(option);
			}
		}
	})
}

var revert = function() {
	$.ajax({
		url: "/manager/revert",
		method: "post",
		data: {
			fileName: fileList.value
		},
		success: function(res) {
			var object = JSON.parse(res);
			debugger;
			var fileList = document.getElementById("fileList");
			for (i of object) {
				var option = document.createElement("option");
				option.text = i['value'];
				option.value = i['value'];
				fileList.add(option);
			}
		}
	})
}

var downloadLogFile = function() {
	let fileName = document.getElementById("logFileList").value;
	$.ajax({
		url: "/manager/downloadLog", // 替換為你的後端API URL
		method: "POST",
		data: JSON.stringify({ fileName: fileName }), // 傳遞的數據，根據需求修改
		contentType: "application/json",
		xhrFields: {
			responseType: 'blob' // 將響應類型設置為Blob
		},
		success: function(data, status, xhr) {
			// 提取文件名（從Content-Disposition中解析）
			const disposition = xhr.getResponseHeader('Content-Disposition');
			let fileName = "downloaded_file";
			if (disposition && disposition.indexOf('filename=') !== -1) {
				const matches = disposition.match(/filename="(.+)"/);
				if (matches.length > 1) fileName = matches[1];
			}

			// 創建Blob對象
			const blob = new Blob([data], { type: xhr.getResponseHeader('Content-Type') });

			// 創建下載鏈接
			const link = document.createElement('a');
			link.href = window.URL.createObjectURL(blob);
			link.download = fileName; // 設置文件名
			document.body.appendChild(link);
			link.click();

			// 清理
			document.body.removeChild(link);
			window.URL.revokeObjectURL(link.href);
		},
		error: function(xhr, status, error) {
			console.error("下載失敗", error);
		}
	});
}

var init = function() {
	var serverStatus = document.getElementById("serverStatus");
	var players = document.getElementById("players");
	//重置內容
	serverStatus.innerHTML = '';
	players.innerHTML = '';
	$.ajax({
		url: "/manager/getPlayers",
		method: "post",
		success: function(res) {
			debugger;
			var object = JSON.parse(res);
			if (!object["livestatus"].includes('2') && !object["livestatus"].includes('0')) {
				serverStatus.innerHTML = "伺服器狀態🟢"; //綠燈
				if (object["players"].length > 0) {
					for (i of object["players"]) {
						players.innerHTML = players.innerHTML + `<input type="text" value="${i}" disabled /><br>`;
					}
				}
			} else {
				serverStatus.innerHTML = "伺服器狀態🔴"; //紅燈
			}
		}
	})
}

window.onload = function() {
	init();
};
