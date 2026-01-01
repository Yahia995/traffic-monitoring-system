import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import AppV2 from "./AppV2";

// Use V2 if enabled, otherwise use V1.5
const enableV2 = import.meta.env.VITE_ENABLE_V2 === 'true';
const AppComponent = enableV2 ? AppV2 : App;

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <AppComponent />
  </React.StrictMode>
);