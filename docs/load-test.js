import http from 'k6/http';
import { check } from 'k6';

export const options = { vus: 10, duration: '30s' };
export default function () {
  const response = http.get(`${__ENV.BASE_URL}/actuator/health`);
  check(response, { 'health is available': (r) => r.status === 200 });
}
