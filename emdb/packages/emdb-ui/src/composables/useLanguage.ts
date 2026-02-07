import { computed } from 'vue';

const DISPLAY_NAMES = new Intl.DisplayNames(['en'], { type: 'language' });

export const SUPPORTED_CODES = [
  'en', // English
  'es', // Spanish
  'fr', // French
  'de', // German
  'it', // Italian
  'pt', // Portuguese
  'ja', // Japanese
  'zh', // Chinese
  'ko', // Korean
  'ru', // Russian
  'hi', // Hindi
  'ar', // Arabic
  'cn', // Cantonese
  'tr', // Turkish
  'th', // Thai
  'vi', // Vietnamese
  'id', // Indonesian
  'nl', // Dutch
  'sv', // Swedish
  'da', // Danish
  'no', // Norwegian
  'fi', // Finnish
  'pl', // Polish
  'hu', // Hungarian
  'cs', // Czech
  'el', // Greek
  'he', // Hebrew
  'fa', // Persian
  'uk', // Ukrainian
  'ro', // Romanian
  'bn', // Bengali
  'ta', // Tamil
  'te', // Telugu
  'ml', // Malayalam
  'mr', // Marathi
  'tl', // Tagalog
  'ms', // Malay
  'sr', // Serbian
  'hr', // Croatian
  'bg', // Bulgarian
  'sk', // Slovak
  'et', // Estonian
  'lt', // Lithuanian
  'lv', // Latvian
  'sl', // Slovenian
  'is', // Icelandic
  'ur', // Urdu
  'sw', // Swahili
] as const;

export function useLanguage() {

  const formatLanguage = (isoCode: string | null): string => {
    if (!isoCode) return 'Unknown';
    try {
      return DISPLAY_NAMES.of(isoCode) || isoCode;
    } catch (e) {
      return isoCode;
    }    
  };

  const languageCodes = computed(() => {
    return SUPPORTED_CODES.map(code => ({ label: formatLanguage(code), value: code }))
      .sort((a, b) => a.label.localeCompare(b.label));
  });

  return { formatLanguage, languageCodes };
}