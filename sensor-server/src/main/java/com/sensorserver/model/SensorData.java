//实体层，数据模型
package com.model; // 改成实际包名

public class SensorData {
    private String sensorId;
    private double temperature;
    private double humidity;
    private double pm25;

    // getter / setter
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public double getPm25() { return pm25; }
    public void setPm25(double pm25) { this.pm25 = pm25; }

    @Override
    public String toString() {
        return "SensorData{" +
                "sensorId='" + sensorId + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pm25=" + pm25 +
                '}';
    }
}