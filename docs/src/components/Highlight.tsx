import React, { type ReactNode } from "react";

interface HighlightProps {
  children: ReactNode;
  color: string;
}

export default function Highlight({ children, color }): ReactNode {
  return (
    <span
      style={{
        backgroundColor: color,
        borderRadius: "2px",
        color: "#000000ff",
        padding: "0.2rem",
      }}
    >
      {children}
    </span>
  );
}
