String formatDate(DateTime date) {
  const months = [
    'ene', 'feb', 'mar', 'abr', 'may', 'jun',
    'jul', 'ago', 'sep', 'oct', 'nov', 'dic',
  ];
  return '${date.day} de ${months[date.month - 1]} de ${date.year}';
}

String formatDateToYYYYMMDD(DateTime date) {
  return date.toIso8601String().split('T')[0];
}

String formatDateToISOWithTimezone(DateTime date) {
  return date.toIso8601String();
}

const String TOKEN_NAME = "ecolim_token";

const String ENTITY_USERS = 'entity_users';
const String USERS = 'users';
const String ENTITIES = 'entities';
const String PLANTS = 'plants';
const String TYPES = 'types';
const String WASTES = 'wastes';
const String PROCESS_FLOWS = 'process_flows';
const String OFFERS = 'waste_offers';