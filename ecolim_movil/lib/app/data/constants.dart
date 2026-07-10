String formatDate(DateTime date) {
  const months = [
    'ene', 'feb', 'mar', 'abr', 'may', 'jun',
    'jul', 'ago', 'sep', 'oct', 'nov', 'dic',
  ];
  return '${date.day} de ${months[date.month - 1]} de ${date.year}';
}

const String ENTITY_USERS = 'entity_users';
const String USERS = 'users';
const String ENTITIES = 'users';