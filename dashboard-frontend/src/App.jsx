import { useState } from "react";
import UploadForm from "./components/UploadForm"
import ViolationsTable from "./components/ViolationsTable"
import "./styles/app.css";

export default function App() {
  const [data, setData] = useState(null);

  return (
    <div className="container">
      <h1>🚦 Traffic Violations Dashboard (MVP)</h1>

      <UploadForm onResult={setData} />

      {data && (
        <>
          <h2>Violations ({data.summary.violations_detected})</h2>
          <p>
            Tracked: {data.summary.total_vehicles_tracked} vehicles | 
            Plates detected: {data.summary.vehicles_with_plates} | 
            Processing time: {data.processing_time_seconds}s
          </p>
          <ViolationsTable violations={data.violations} />
        </>
      )}
    </div>
  );
}