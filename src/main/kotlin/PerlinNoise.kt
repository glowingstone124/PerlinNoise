package ind.glowingstone

import kotlin.math.floor
import kotlin.math.pow


class Vec2D(val x: Double, val y: Double) {
	infix fun dot(v: Vec2D) = x * v.x + y * v.y
}


class PerlinNoise(val seed: String) {

	fun Double.fastpow(exp: Int): Double {
		if (exp == 0) return 1.0
		if (exp == 1) return this
		var result = 1.0
		var b = this
		var e = exp
		while (e > 0) {
			if (e and 1 == 1) result *= b
			b *= b
			e = e shr 1
		}
		return result
	}
	val chars: CharArray = seed.padEnd(128, '\u0000').take(128).toCharArray()

	val A = 0.5

	val fin: Long = chars.copyOfRange(0, 64).fold(0L) { acc, c ->
		acc * 31 + c.code
	} + chars.copyOfRange(64, 128).fold(0L) { acc, c ->
		acc * 31 + c.code
	}

	private val gradientCache = HashMap<Pair<Int, Int>, Vec2D>()

	inline fun lerp(a: Double, b: Double, t: Double): Double = a + (b - a) * t

	inline fun S(t: Double) = 6 * t.fastpow(5) - 15 * t.fastpow(4) + 10 * t.fastpow(3)


	fun generateRandomGradient(input: Vec2D): Vec2D {
		val key = Pair(floor(input.x).toInt(), floor(input.y).toInt())
		gradientCache[key]?.let { return it }

		var s = (key.first * 374761393 + key.second * 668265263 + fin).toLong()
		s = (s xor (s shr 13)) * 1274126177L
		s = s xor (s shr 16)

		val angle = (s and 7L) * 45.0
		val rad = Math.toRadians(angle)
		val vec = Vec2D(kotlin.math.cos(rad), kotlin.math.sin(rad))
		gradientCache[key] = vec
		return vec
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