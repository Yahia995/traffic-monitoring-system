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
          <h2>ViolationsTable ({data.violations_nbr})</h2>
          <ViolationsTable violations={data.violations} />

          <details>
            <summary>Raw JSON</summary>
            <pre>{JSON.stringify(data, null, 2)}</pre>
          </details>
        </>
      )}
    </div>
  );
}