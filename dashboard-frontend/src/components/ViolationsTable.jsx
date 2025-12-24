export default function ViolationsTable({ violations }) {
  if(!violations || Object.keys(violations).length === 0)
    return <p>No violations detected</p>;

  return (
    <table>
      <thead>
        <tr>
          <th>Plate</th>
          <th>Speed</th>
          <th>Limit</th>
          <th>Timestamp (s)</th>
        </tr>
      </thead>
      <tbody>
        // ViolationsTable.jsx
        {violations.map((v) => (
          <tr key={v.violation_id} className={`severity-${v.severity}`}>
            <td>{v.plate_number}</td>
            <td>
              {v.speed_kmh.toFixed(2)}
              {!v.plate_validated && <span className="warning">⚠️</span>}
            </td>
            <td>{v.speed_limit_kmh}</td>
            <td>{v.overspeed_kmh.toFixed(2)}</td>
            <td>{v.timestamp_seconds.toFixed(2)}</td>
            <td>
              <span className={`badge badge-${v.severity}`}>
                {v.severity}
              </span>
            </td>
            <td>{(v.plate_confidence * 100).toFixed(0)}%</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}