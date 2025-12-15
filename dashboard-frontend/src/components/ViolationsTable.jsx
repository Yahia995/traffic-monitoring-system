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
        {Object.entries(violations).map(([plate, v]) => (
          <tr key={plate}>
            <td>{plate}</td>
            <td>{v.speed.toFixed(2)}</td>
            <td>{v.speed_limit}</td>
            <td>{v.timestamp.toFixed(2)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}