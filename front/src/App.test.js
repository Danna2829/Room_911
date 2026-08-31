import { render, screen } from "@testing-library/react";
import App from "./App";

test("muestra identificación por ID interno sin contraseña", () => {
  render(<App />);
  expect(screen.getByLabelText(/ID interno/i)).toBeInTheDocument();
  expect(screen.queryByLabelText(/contraseña/i)).not.toBeInTheDocument();
});
