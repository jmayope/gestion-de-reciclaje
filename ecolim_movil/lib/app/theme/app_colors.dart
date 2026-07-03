import 'package:flutter/material.dart';

/// Tokens de color del sistema de diseño "EcoGestión".
///
/// Dirección visual: minimalista claro con acentos verdes, evitando los
/// clichés de "verde bosque genérico". Se usa un verde pino profundo como
/// color de marca/confianza (residuos = responsabilidad institucional) y
/// un verde hoja vibrante como acento de acción (procesos activos,
/// valorización). El rojo-terracota se reserva únicamente para residuos
/// peligrosos, dándole significado semántico real dentro del dominio.
class AppColors {
  AppColors._();

  // ---- Marca ----
  static const Color pine900 = Color(0xFF0F5C48); // Primario / marca
  static const Color pine700 = Color(0xFF15735A);
  static const Color leaf500 = Color(0xFF2BAE7E); // Acento / acción principal
  static const Color leaf300 = Color(0xFF6FD3A8);
  static const Color leafSoft = Color(0xFFE3F5EC); // Fondos de chips/estado

  // ---- Neutros (con tinte verde-grisáceo, no gris puro) ----
  static const Color ink900 = Color(0xFF10201B); // Texto principal
  static const Color ink600 = Color(0xFF44554F); // Texto secundario
  static const Color ink400 = Color(0xFF7C8C86); // Texto terciario / hints
  static const Color line200 = Color(0xFFDCE6E1); // Bordes sutiles
  static const Color surfaceLight = Color(0xFFFFFFFF);
  static const Color bgLight = Color(0xFFF6FAF8);

  // ---- Modo oscuro ----
  static const Color bgDark = Color(0xFF0B1512);
  static const Color surfaceDark = Color(0xFF122420);
  static const Color surfaceDarkAlt = Color(0xFF17302A);
  static const Color line700 = Color(0xFF243D36);
  static const Color textDarkPrimary = Color(0xFFEAF5F0);
  static const Color textDarkSecondary = Color(0xFFA9C1B8);
  static const Color leafBright = Color(0xFF3ECF8E); // Acento en oscuro

  // ---- Semántica de dominio ----
  static const Color hazard = Color(0xFFD9534F); // Residuo peligroso
  static const Color hazardSoft = Color(0xFFFBEAE9);
  static const Color warning = Color(0xFFE0A72E); // Pendiente / en revisión
  static const Color warningSoft = Color(0xFFFBF1DD);
  static const Color info = Color(0xFF2E86E0); // Operador / oferta
  static const Color infoSoft = Color(0xFFE7F1FC);
}
