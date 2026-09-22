const ICONS = {
  sunny: `<svg viewBox="0 0 64 64" class="icon"><circle cx="32" cy="32" r="13" fill="#F2A93C"/><g stroke="#F2A93C" stroke-width="4" stroke-linecap="round"><line x1="32" y1="4" x2="32" y2="12"/><line x1="32" y1="52" x2="32" y2="60"/><line x1="4" y1="32" x2="12" y2="32"/><line x1="52" y1="32" x2="60" y2="32"/><line x1="12" y1="12" x2="17.5" y2="17.5"/><line x1="46.5" y1="46.5" x2="52" y2="52"/><line x1="12" y1="52" x2="17.5" y2="46.5"/><line x1="46.5" y1="17.5" x2="52" y2="12"/></g></svg>`,
  partlyCloudy: `<svg viewBox="0 0 64 64" class="icon"><circle cx="24" cy="26" r="11" fill="#F2A93C"/><path d="M17 44a11 11 0 0 1 1-22 14 14 0 0 1 27 4 10 10 0 0 1-2 40H17a9 9 0 0 1 0-22z" fill="#B9D6E8"/><path d="M17 44a11 11 0 0 1 1-22 14 14 0 0 1 27 4 10 10 0 0 1-2 22H19a9 9 0 0 1-2-4z" fill="#FFFFFF"/></svg>`,
  cloudy: `<svg viewBox="0 0 64 64" class="icon"><path d="M18 46a12 12 0 0 1 1-24 15 15 0 0 1 29 5 11 11 0 0 1-2 44H19a10 10 0 0 1-1-25z" fill="#9FBBCB"/><path d="M20 44a11 11 0 0 1 1-22 14 14 0 0 1 26 5 10 10 0 0 1-2 40H21a9 9 0 0 1-1-23z" fill="#FFFFFF"/></svg>`,
  rain: `<svg viewBox="0 0 64 64" class="icon"><path d="M18 38a11 11 0 0 1 1-21 14 14 0 0 1 26 4 10 10 0 0 1-2 38H19a9 9 0 0 1-1-21z" fill="#B9D6E8"/><g stroke="#2E6DA4" stroke-width="3.4" stroke-linecap="round"><line x1="24" y1="46" x2="21" y2="56"/><line x1="34" y1="46" x2="31" y2="56"/><line x1="44" y1="46" x2="41" y2="56"/></g></svg>`,
  storm: `<svg viewBox="0 0 64 64" class="icon"><path d="M18 34a11 11 0 0 1 1-21 14 14 0 0 1 26 4 10 10 0 0 1-2 34H19a9 9 0 0 1-1-17z" fill="#7C93A4"/><polygon points="35,38 26,52 32,52 28,60 42,44 35,44" fill="#F2A93C"/></svg>`,
  fog: `<svg viewBox="0 0 64 64" class="icon"><path d="M20 30a10 10 0 0 1 1-18 13 13 0 0 1 24 3 9 9 0 0 1-2 30H21a8 8 0 0 1-1-15z" fill="#C7D8E1"/><g stroke="#8CA3B2" stroke-width="3.2" stroke-linecap="round"><line x1="12" y1="42" x2="52" y2="42"/><line x1="16" y1="50" x2="48" y2="50"/><line x1="20" y1="58" x2="44" y2="58"/></g></svg>`,
  snow: `<svg viewBox="0 0 64 64" class="icon"><path d="M18 36a11 11 0 0 1 1-21 14 14 0 0 1 26 4 10 10 0 0 1-2 36H19a9 9 0 0 1-1-19z" fill="#C7D8E1"/><g stroke="#4C6577" stroke-width="2.6" stroke-linecap="round"><line x1="24" y1="46" x2="24" y2="56"/><line x1="20" y1="51" x2="28" y2="51"/><line x1="40" y1="46" x2="40" y2="56"/><line x1="36" y1="51" x2="44" y2="51"/></g></svg>`,
  drizzle: `<svg viewBox="0 0 64 64" class="icon"><path d="M20 36a10 10 0 0 1 1-19 13 13 0 0 1 24 4 9 9 0 0 1-2 33H21a8 8 0 0 1-1-18z" fill="#C7D8E1"/><g stroke="#4EA3D6" stroke-width="2.8" stroke-linecap="round"><line x1="26" y1="46" x2="24" y2="52"/><line x1="36" y1="46" x2="34" y2="52"/></g></svg>`,
};

function pickIcon(desc){
  const d = (desc || '').toLowerCase();
  if (/trovoada|tempestade|raio/.test(d)) return ICONS.storm;
  if (/neve|nevando|gelo/.test(d)) return ICONS.snow;
  if (/garoa|chuvisco/.test(d)) return ICONS.drizzle;
  if (/chuva|chuvoso|pancada/.test(d)) return ICONS.rain;
  if (/névoa|neblina|nevoeiro|bruma/.test(d)) return ICONS.fog;
  if (/sol/.test(d) && /(nuvens|nublado|nebulosidade)/.test(d)) return ICONS.partlyCloudy;
  if (/nublado|encoberto|nuvens/.test(d)) return ICONS.cloudy;
  if (/sol|limpo|claro/.test(d)) return ICONS.sunny;
  return ICONS.cloudy;
}
