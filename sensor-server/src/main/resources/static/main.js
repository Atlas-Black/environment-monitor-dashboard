// ===================== main.js =====================
document.addEventListener("DOMContentLoaded", () => {
    console.log("✅ main.js 已加载");

    const apiUrl = "http：//localhost:8080/api/sensor/latest";
    const analysisUrl = "http://localhost:8080/api/analysis/latest";

    // 报警日志
    const logBox = document.createElement('div');
    logBox.id = 'alert-log';
    logBox.style.marginTop = '20px';
    logBox.style.padding = '10px';
    logBox.style.borderTop = '2px solid #ccc';
    logBox.style.fontFamily = 'monospace';
    logBox.style.maxHeight = '200px';
    logBox.style.overflowY = 'auto';
    logBox.innerHTML = "<h3>📜 报警日志</h3>";
    document.body.appendChild(logBox);

    let alertLogs = [];
    const ctx = document.getElementById("sensorChart");
    const dataBox = document.getElementById("data");
    const table = document.getElementById("data-table");

    // ===================== Chart 初始化 =====================
    const chartData = {
        labels: [],
        datasets: [
            { label: "温度 (°C)", data: [], borderColor: "rgba(255,99,132,1)", backgroundColor: "rgba(255,99,132,0.2)", tension: 0, fill: false, borderWidth: 2, pointRadius: 4 },
            { label: "湿度 (%)", data: [], borderColor: "rgba(54,162,235,1)", backgroundColor: "rgba(54,162,235,0.2)", tension: 0, fill: false, borderWidth: 2, pointRadius: 4 },
            { label: "PM2.5 (μg/m³)", data: [], borderColor: "rgba(75,192,192,1)", backgroundColor: "rgba(75,192,192,0.2)", tension: 0, fill: false, borderWidth: 2, pointRadius: 4 },
            { label: "温度预测 (°C)", data: [], borderColor: "rgba(255,159,64,1)", borderDash: [10,5], fill: false, borderWidth: 2, pointRadius: 0 }
        ]
    };

    const chart = new Chart(ctx, {
        type: "line",
        data: chartData,
        options: {
            responsive: true,
            plugins: {
                legend: { position: "top", labels: { font: { size: 14 }, color: "#333" } },
                title: { display: true, text: "🌡️ 实时环境监测折线图", color: "#222", font: { size: 18, weight: "bold" } }
            },
            animation: { duration: 800, easing: "easeInOutQuart" },
            scales: { x: { ticks: { color: "#555" }, grid: { color: "rgba(200,200,200,0.3)" } },
                      y: { beginAtZero: true, ticks: { color: "#555" }, grid: { color: "rgba(200,200,200,0.3)" } } }
        }
    });

    // ===================== 报警模块 =====================
    const alertBox = document.createElement('div');
    alertBox.id = 'alert-box';
    alertBox.style.position = 'fixed';
    alertBox.style.top = '20px';
    alertBox.style.right = '20px';
    alertBox.style.padding = '15px 25px';
    alertBox.style.borderRadius = '8px';
    alertBox.style.fontSize = '18px';
    alertBox.style.fontWeight = 'bold';
    alertBox.style.display = 'none';
    alertBox.style.color = '#fff';
    alertBox.style.backgroundColor = '#e74c3c';
    alertBox.style.boxShadow = '0 0 10px rgba(231,76,60,0.7)';
    alertBox.style.zIndex = '9999';
    document.body.appendChild(alertBox);

    const alarmSound = new Audio('https://actions.google.com/sounds/v1/alarms/alarm_clock.ogg');
    alarmSound.loop = true;
    let soundEnabled = false;
    document.getElementById("enableSound").addEventListener("click", () => {
        alarmSound.play().then(() => { soundEnabled = true; document.getElementById("enableSound").style.display='none'; })
        .catch(err => console.warn("⚠️ 播放被阻止", err));
    });

    function checkAlert(sensor) {
        let message = '';
        if(sensor.temperature>35) message += `🌡️ 高温警报：${sensor.temperature.toFixed(1)}°C<br>`;
        if(sensor.pm25>75) message += `🌫️ 空气污染警报：PM2.5=${sensor.pm25.toFixed(1)} μg/m³<br>`;

        if(message){
            alertBox.innerHTML = message;
            alertBox.style.display = 'block';
            if(soundEnabled) alarmSound.play().catch(()=>{});
            const time = new Date().toLocaleTimeString();
            const logMsg = `${time} ⚠️ ${message.replace(/<br>/g,' ')}`;
            alertLogs.push(logMsg);
            if(alertLogs.length>10) alertLogs.shift();
            logBox.innerHTML = "<h3>📜 报警日志</h3>" + alertLogs.map(msg=>`<div>${msg}</div>`).join('');
        } else {
            alertBox.style.display = 'none';
            if(soundEnabled){ alarmSound.pause(); alarmSound.currentTime=0; }
        }
    }

    // ===================== 更新折线图 =====================
    function updateChartWithTrend(latest, trendData) {
        const now = new Date().toLocaleTimeString();
        chartData.labels.push(now);
        if(chartData.labels.length>10) chartData.labels.shift();

        chartData.datasets[0].data.push(latest.temperature);
        chartData.datasets[1].data.push(latest.humidity);
        chartData.datasets[2].data.push(latest.pm25);
        chartData.datasets[0].data.length>10 && chartData.datasets[0].data.shift();
        chartData.datasets[1].data.length>10 && chartData.datasets[1].data.shift();
        chartData.datasets[2].data.length>10 && chartData.datasets[2].data.shift();

        chartData.datasets[3].data = trendData; // 预测虚线
        chart.update();
    }

    // ===================== 获取数据 =====================
    async function fetchData() {
        try{
            // 实时数据
            const resp = await fetch(apiUrl);
            const latest = await resp.json();
            //页面顶部实时数据显示
            dataBox.innerHTML = `
                🌡️ 温度：${latest.temperature.toFixed(1)}°C<br>
                💧 湿度：${latest.humidity.toFixed(1)}%<br>
                🌫️ PM2.5：${latest.pm25.toFixed(1)} μg/m³
            `;
            //表格更新
            table.innerHTML = `<tr><th>传感器ID</th><th>温度</th><th>湿度</th><th>PM2.5</th></tr>
                <tr><td>${latest.sensorId}</td><td>${latest.temperature}</td><td>${latest.humidity}</td><td>${latest.pm25}</td></tr>`;
            //更新按钮下方的实时数据
              document.getElementById("temperature").innerText = latest.temperature.toFixed(1);
              document.getElementById("humidity").innerText = latest.humidity.toFixed(1);
              document.getElementById("pm25").innerText = latest.pm25.toFixed(1);

            // 预测数据
            const analysisResp = await fetch(analysisUrl);
            const analysis = await analysisResp.json();
            const trendData = analysis.trendData || [];

            updateChartWithTrend(latest, trendData);
            checkAlert(latest);
        } catch(e){
            console.error("❌ 获取数据失败:", e);
        }
    }

    fetchData();
    setInterval(fetchData, 3000);
});

// ===================== PDF报表 =====================
document.getElementById("generateReport").addEventListener("click", async () => {
    const { jsPDF } = window.jspdf;

    // 用 html2canvas 把整个网页渲染成一张图
    const canvas = await html2canvas(document.body, {
        scale: 2,
        useCORS: true,
        scrollY: 0
    });

    const imgData = canvas.toDataURL('image/png');

    // 创建 A4 尺寸 PDF
    const pdf = new jsPDF({
        orientation: 'p',
        unit: 'mm',
        format: 'a4'
    });

    // 计算比例，让图片适应 A4 页面
    const imgWidth = 210;
    const pageHeight = 297;
    const imgHeight = canvas.height * imgWidth / canvas.width;

    // 如果图片超出一页高度，则缩放到一页内
    const height = imgHeight > pageHeight ? pageHeight - 10 : imgHeight;

    pdf.addImage(imgData, 'PNG', 0, 0, imgWidth, height);
    pdf.save("Sensor_Report.pdf");
});
