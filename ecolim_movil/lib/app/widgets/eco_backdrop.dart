import 'package:flutter/material.dart';
import '../theme/app_colors.dart';

/// Elemento de firma visual de la app: una forma orgánica tipo "hoja/gota"
/// que ancla la parte superior de las pantallas de autenticación.
/// Se usa con moderación (solo en Splash, Login, Registro) para dar
/// identidad sin saturar pantallas de trabajo (dashboard, formularios).
class EcoBackdrop extends StatelessWidget {
  final double height;
  final Widget? child;

  const EcoBackdrop({super.key, this.height = 260, this.child});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return SizedBox(
      height: height,
      width: double.infinity,
      child: Stack(
        clipBehavior: Clip.none,
        children: [
          ClipPath(
            clipper: _LeafClipper(),
            child: Container(
              height: height,
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: isDark
                      ? [AppColors.pine700, AppColors.bgDark]
                      : [AppColors.pine900, AppColors.leaf500],
                ),
              ),
            ),
          ),
          // Vena orgánica sutil, referencia directa al mundo del residuo
          // orgánico / vegetal reutilizado como material de diseño.
          Positioned.fill(
            child: ClipPath(
              clipper: _LeafClipper(),
              child: CustomPaint(painter: _VeinPainter()),
            ),
          ),
          if (child != null) Positioned.fill(child: child!),
        ],
      ),
    );
  }
}

class _LeafClipper extends CustomClipper<Path> {
  @override
  Path getClip(Size size) {
    final path = Path();
    path.lineTo(0, size.height * 0.72);
    path.quadraticBezierTo(
      size.width * 0.28,
      size.height,
      size.width * 0.55,
      size.height * 0.88,
    );
    path.quadraticBezierTo(
      size.width * 0.85,
      size.height * 0.74,
      size.width,
      size.height * 0.9,
    );
    path.lineTo(size.width, 0);
    path.close();
    return path;
  }

  @override
  bool shouldReclip(CustomClipper<Path> oldClipper) => false;
}

class _VeinPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = Colors.white.withOpacity(0.10)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.4;

    final path = Path()
      ..moveTo(size.width * 0.1, size.height * 0.15)
      ..quadraticBezierTo(size.width * 0.45, size.height * 0.05,
          size.width * 0.9, size.height * 0.28);
    canvas.drawPath(path, paint);

    for (final dx in [0.28, 0.5, 0.7]) {
      final branch = Path()
        ..moveTo(size.width * dx, size.height * 0.12)
        ..quadraticBezierTo(
          size.width * (dx + 0.06),
          size.height * 0.3,
          size.width * (dx + 0.02),
          size.height * 0.46,
        );
      canvas.drawPath(branch, paint..color = Colors.white.withOpacity(0.07));
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
