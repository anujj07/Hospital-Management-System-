import { render, screen } from '@testing-library/react';
import Navbar from './components/home/Navbar';

test('renders navbar with site title', () => {
  render(<Navbar />);
  const titleElement = screen.getByText(/LifeBridge Hospital/i);
  expect(titleElement).toBeInTheDocument();
});
