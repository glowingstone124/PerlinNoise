package ind.glowingstone

import kotlin.math.floor
import kotlin.math.pow


data class Vec2D(val x: Double, val y: Double) {
	infix fun dot(v: Vec2D) = x * v.x + y * v.y
}

class PerlinNoise(val seed: String) {

	val chars: CharArray = seed.padEnd(128, '\u0000').take(128).toCharArray()

	val A = 0.5

	val fin: Long = chars.copyOfRange(0, 64).fold(0L) { acc, c ->
		acc * 31 + c.code
	} + chars.copyOfRange(64, 128).fold(0L) { acc, c ->
		acc * 31 + c.code
	}


	fun lerp(a: Double, b: Double, t: Double): Double = a + (b - a) * t

	fun S(t: Double) = 6 * t.pow(5) - 15 * t.pow(4) + 10 * t.pow(3)

	fun generateRandomGradient(input: Vec2D): Vec2D {
		var s = (input.x * 374761393 + input.y * 668265263 + fin).toLong()
		s = (s xor (s shr 13)) * 1274126177L
		s = s xor (s shr 16)

		val angles = doubleArrayOf(
			0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0
		)
		val angle = angles[(s and 7L).toInt()]
		val rad = Math.toRadians(angle)
		return Vec2D(kotlin.math.cos(rad), kotlin.math.sin(rad))
	}
	fun getPointU(input: Vec2D): Double {
		return input.x - floor(input.x)
	}

	fun getPointV(input: Vec2D): Double {
		return input.y - floor(input.y)
	}

	fun calculateN(input: Vec2D): Double {
		val i = floor(input.x)
		val j = floor(input.y)
		val g00 = generateRandomGradient(Vec2D(i, j))
		val g01 = generateRandomGradient(Vec2D(i, j + 1))
		val g10 = generateRandomGradient(Vec2D(i + 1, j))
		val g11 = generateRandomGradient(Vec2D(i + 1, j + 1))
		val d00 = g00 dot Vec2D(getPointU(input), getPointV(input))
		val d01 = g01 dot Vec2D(getPointU(input), getPointV(input) - 1)
		val d10 = g10 dot Vec2D(getPointU(input) - 1, getPointV(input))
		val d11 = g11 dot Vec2D(getPointU(input) - 1, getPointV(input) - 1)

		return lerp(lerp(d00, d10, S(getPointU(input))), lerp(d01, d11, S(getPointU(input))), S(getPointV(input)))
	}

	fun PSigma(input: Vec2D, octaves: Int): Double {
		var result = 0.0
		for (i: Int in 0..<octaves) {
			result += A.pow(i) * calculateN(Vec2D(2.0.pow(i.toDouble()) * input.x, 2.0.pow(i.toDouble()) * input.y))
		}
		return result
	}
}