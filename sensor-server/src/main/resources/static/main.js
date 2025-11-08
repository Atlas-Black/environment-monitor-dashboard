// ===================== main.js =====================

// 定期从后端获取最新传感器数据并更新图表
const apiUrl = "http://localhost:8080/api/sensor/data";

const ctx = document.getElementById("chart").getContext("2d");
let chart;

// 初始化空图表
function initChart() {
  chart = new Chart(ctx, {
    type: "line",
    data: {
      labels: [],
      datasets: [
        {
          label: "温度 (°C)",
          data: [],
          borderWidth: 2,
          fill: false,
          tension: 0.1,
        },
        {
          label: "湿度 (%)",
          data: [],
          borderWidth: 2,
          fill: false,
          tension: 0.1,
        },
        {
          label: "PM2.5 (μg/m³)",
          data: [],
          borderWidth: 2,
          fill: false,
          tension: 0.1,
        },
      ],
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: true,
        },
      },
    },
  });
}

// 获取数据并更新显示
async function fetchData() {
  try {
    const response = await fetch(apiUrl);
    const data = await response.json();

    if (data.length === 0) {
      document.getElementById("data").innerText = "暂无数据...";
      return;
    }

    // 取最近 10 条数据
    const latest = data.slice(-10);

    const labels = latest.map(item =>
      new Date(item.timestamp).toLocaleTimeString()
    );
    const temperatures = latest.map(item => item.temperature);
    const humidity = latest.map(item => item.humidity);
    const pm25 = latest.map(item => item.pm25);

    // 更新文字显示
    const last = latest[latest.length - 1];
    document.getElementById("data").innerHTML = `
      🌡️ 温度：${last.temperature.toFixed(1)}°C<br>
      💧 湿度：${last.humidity.toFixed(1)}%<br>
      🌫️ PM2.5：${last.pm25.toFixed(1)} μg/m³
    `;

    // 更新图表数据
    chart.data.labels = labels;
    chart.data.datasets[0].data = temperatures;
    chart.data.datasets[1].data = humidity;
    chart.data.datasets[2].data = pm25;
    chart.update();

  } catch (error) {
    console.error("❌ 获取数据失败：", error);
  }
}

// 每 3 秒更新一次
setInterval(fetchData, 3000);

window.onload = () => {
  initChart();
  fetchData();
};