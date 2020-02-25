package org.jmulti.calc

import java.io.PrintWriter

import org.jmulti.{CalcParams, Logger, ParametersSweep, UnitCell}
import org.jmulti.tables.{AtomicFactor, Charge, DispersionCorrectionFS}

import scala.annotation.strictfp
import scala.util.{Failure, Success, Try}
import scala.collection.parallel.CollectionConverters._

class Calc {
  val pi:Double = Math.PI
  @strictfp val rad:Double = Math.PI / 180.0
  val edi:Complex = Complex(0, 1)

  private var dispCorrectionMemo = Map[String,Complex]()

  def reset():Unit = {
    dispCorrectionMemo = Map()
  }

  @strictfp def getDispCorrection(sym:String, wavelength:Double):Try[Complex] = {
    val result = dispCorrectionMemo.get(sym)
    if (result.isEmpty) {
      dispCorrectionMemo.synchronized {
        val r = dispCorrectionMemo.get(sym)
        if (r.isEmpty) {
          val c = DispersionCorrectionFS(sym, wavelength)
          if (c.isSuccess) {
            dispCorrectionMemo = dispCorrectionMemo + (sym -> c.getOrElse(Complex(0, 0)))
          }
          c
        } else Success(r.get)
      }
    } else Success(result.get)
  }

  @strictfp def atomic_factor(atom:String, charge:Int, sl:Double, wavelength:Double): Try[Complex] = for {
    corr <- getDispCorrection(atom, wavelength)
    f    <- AtomicFactor(atom, charge, sl)
  } yield corr + f

  def atom_charge(charge:Int):String = {
    val sign = if (charge  > 0) "+" else "-"
    s"${Math.abs(charge)}$sign"
  }

  @strictfp def sumTryComplex(a:Try[Complex], b:Try[Complex]):Try[Complex] =
    (a,b) match {
      case (Success(a), Success(b)) => Success(a+b)
      case (err:Failure[_], _) => err
      case (_, err:Failure[_]) => err
    }

  @strictfp def structure_factor(uc:UnitCell, h:Double, k:Double, l:Double, atoms:Array[AtomDescr], wavelength:Double): Try[Complex] ={
    val sl = 0.5*Math.sqrt(uc.d_hkl(h, k, l))
    val f = atoms.map( a => atomic_factor(a.name, a.charge, sl, wavelength).map(_ * cexp(2 * pi * (h*a.p.x + k*a.p.y + l*a.p.z) * edi)))
    f reduce sumTryComplex
  }

  @strictfp def apply(uc:UnitCell, atoms:Array[AtomDescr], params: CalcParams): Unit = {
    val parallel = if (params.parallelCalc) "par" else "seq"
    val filename = params.sweep match {
      case ParametersSweep.PSI =>
        s"${params.title}-(${params.h}${params.k}${params.l})-E ${params.energyStart}-psi ${params.psiStart} ${params.psiEnd} ${params.psiSteps} $parallel"
      case ParametersSweep.ENERGY  =>
        s"${params.title}-(${params.h}${params.k}${params.l})-E ${params.energyStart} ${params.energyEnd} ${params.energySteps}-psi ${params.psiStart} $parallel"
      case ParametersSweep.BOTH =>
        s"${params.title}-(${params.h}${params.k}${params.l})-E ${params.energyStart} ${params.energyEnd} ${params.energySteps}-psi ${params.psiStart} ${params.psiEnd} ${params.psiSteps} $parallel"
    }

    // calculated theta for main reflection in degrees
    val theta = Math.asin(0.5 * Math.sqrt(uc.d_hkl(params.h, params.k, params.l)) * (12.398519 / params.energyStart)) / rad

    val outWriter = new PrintWriter(s"params$filename.txt")

    outWriter.println(f"${uc.a}%18.8f    -lattice const a")
    outWriter.println(f"${uc.b}%18.8f    - lattice const b")
    outWriter.println(f"${uc.c}%18.8f    - lattice const c")
    outWriter.println(f"${uc.alpha}%18.8f    - alpha (grad) 90 for orthorhombic unit cell")
    outWriter.println(f"${uc.beta}%18.8f    - beta (grad) 90 for orthorhombic unit cell")
    outWriter.println(f"${uc.gamma}%18.8f    - gamma (grad) 90 for orthorhombic unit cell")
    outWriter.println(f"$theta%18.8f    - Bragg angle thetaB (grad)")
    outWriter.println(f"${12.398519 / params.energyStart}%18.8f    - wavelength (angstrom)")
    outWriter.println(f"${params.energyStart}%18.8f    - Ex- x-ray energy")
    outWriter.println()

    for (atom <- atoms) {
      outWriter.println(s"${atom.name} ${atom.p.x} ${atom.p.y} ${atom.p.z}")
    }
    outWriter.close()

    val azimLin = params.sweep match {
      case ParametersSweep.PSI =>
        val w = new PrintWriter (s"azim-lin-$filename.csv")
        w.println(s"Psi;h;k;l;fMod2ss;fMod2pp;fMod2ps;fMod2sp;fMod2s;fMod2p;fMulssRe;fMulssIm;fMulppRe;fMulppIm;fMulpsRe;fMulpsIm;fMulspRe;fMulspIm;fQQRe;fQQIm;fDQRe;fDQIm;fMagRe;fMagIm;Energy=${params.energyStart}")
        w
      case ParametersSweep.ENERGY =>
        val w = new PrintWriter (s"azim-lin-$filename.csv")
        w.println(s"Energy;h;k;l;fMod2ss;fMod2pp;fMod2ps;fMod2sp;fMod2s;fMod2p;fMulssRe;fMulssIm;fMulppRe;fMulppIm;fMulpsRe;fMulpsIm;fMulspRe;fMulspIm;fQQRe;fQQIm;fDQRe;fDQIm;fMagRe;fMagIm;Psi=${params.psiStart}")
        w
      case ParametersSweep.BOTH =>
        val w = new PrintWriter (s"Rl2-field-$filename.csv")
        val psiValues = (1 to (params.psiSteps + 1)) map {
          i =>
            val psi = params.psiStart + (i - 1) * (params.psiEnd - params.psiStart) / params.psiSteps
            psi.toString
        }
        w.println(s"Energy\\Psi;${psiValues.mkString(";")}")
        w
    }

    val azimCirc = params.sweep match {
      case ParametersSweep.PSI =>
        val w = new PrintWriter (s"azim-circ-$filename.csv")
        w.println(s"Psi;h;k;l;Rr2;Rl2;(Rr2-Rl2)/(Rr2+Rl2);(Rr2+Rl2)/2;Energy=${params.energyStart}")
        w
      case ParametersSweep.ENERGY =>
        val w = new PrintWriter (s"azim-circ-$filename.csv")
        w.println(s"Energy;h;k;l;Rr2;Rl2;(Rr2-Rl2)/(Rr2+Rl2);(Rr2+Rl2)/2;Psi=${params.psiStart}")
        w
      case ParametersSweep.BOTH =>
        val w = new PrintWriter (s"Rr2-field-$filename.csv")
        val psiValues = (1 to (params.psiSteps + 1)) map {
          i =>
            val psi = params.psiStart + (i - 1) * (params.psiEnd - params.psiStart) / params.psiSteps
            psi.toString
        }
        w.println(s"Energy\\Psi;${psiValues.mkString(";")}")
        w
    }

    val peaks = params.sweep match {
      case ParametersSweep.BOTH =>
        val p = new PrintWriter(s"peaks-hkl-$filename.dat")
        val psiValues = (1 to (params.psiSteps + 1)) map {
          i =>
            val psi = params.psiStart + (i - 1) * (params.psiEnd - params.psiStart) / params.psiSteps
            psi.toString
        }
        p.println(s"Energy\\Psi;${psiValues.mkString(";")}")
        p
      case _ => null
    }

    for (e <- 1 to (params.energySteps + 1)) {
      val energy = if (params.energySteps == 0) {
        params.energyStart
      } else {
        params.energyStart + (e - 1) * (params.energyEnd - params.energyStart) / params.energySteps
      }

      if (params.sweep == ParametersSweep.BOTH) {
        azimCirc.print(s"$energy")
        azimLin.print(s"$energy")
        peaks.print(s"$energy")
      }

      reset()

      // FIXME: Deal with magnetic effects
      val fMag = Complex(0, 0)
      val fDQ = 0.0 * Complex(0.0, 1.0) // this is an imitation of imaginary magnetic scattering
      val fQQ = 0.0 * Complex(-1.0, 1.0)

      val r0: Double = 2.81794092e-5d //classical electron radius in Angstrom

      val lambda = 12.398519 / energy // a calculated lambda in A, Ex(keV) was read above

      // calculated thetab for main reflection in degrees
      val thetab = Math.asin(0.5 * Math.sqrt(uc.d_hkl(params.h, params.k, params.l)) * lambda) / rad
      val wavevec = 2 * pi / lambda // in A^{-1}
      params.sweep match {
        case ParametersSweep.PSI =>
          Logger.log(s"${params.h}, ${params.k}, ${params.l}, $pi, $r0, thetab = $thetab")
        case ParametersSweep.ENERGY =>
          if (e == 1) {
            Logger.log(s"${params.h}, ${params.k}, ${params.l}, $pi, $r0")
          }
          Logger.log(s"Energy = $energy, thetab = $thetab")
        case ParametersSweep.BOTH =>
          Logger.log(s"Energy = $energy, thetab = $thetab")
      }
      //val vol=uc.a*uc.b*uc.c //A^3
      val Array(a, b, c) = uc.getUnitCellVectors
      val vol = P3.tripleProduct(a, b, c) // A^3
      // reciprocal lattice vectors
      val aRec = 2.0 * pi * (b cross c) / vol
      val bRec = 2.0 * pi * (c cross a) / vol
      val cRec = 2.0 * pi * (a cross b) / vol
      val atom_count = atoms.foldLeft(Map[String, Double]())((c, a) => c + (a.name -> (c.getOrElse(a.name, 0.0) + 1.0)))
      val t = atom_count.toList map {
        case (sym, cnt) => for {
          charge <- Charge(sym)
          corr <- getDispCorrection(sym, lambda)
        } yield cnt * (charge + corr)
      }
      val t2 = t.reduce(sumTryComplex)
      if (t2.isFailure) {
        Logger.log(t2.failed.get.getMessage)
        return
      }
      val chi0 = -4.0 * pi * r0 * t2.get / (wavevec ** 2 * vol)
      if (e == 1) {
        Logger.log(s"chi0 = $chi0")
      }

      // now we create an array of umweg reflections with corresponding Fh1*Fh2

      val indices = for {
        ah <- (-params.namax / 2) to (params.namax / 2)
        bh <- (-params.nbmax / 2) to (params.nbmax / 2)
        ch <- (-params.ncmax / 2) to (params.ncmax / 2)
        if 0 != ah && 0 != bh && 0 != ch
        ah2 = params.h - ah
        bh2 = params.k - bh
        ch2 = params.l - ch
        if 0 != ah2 && 0 != bh2 && 0 != ch2
        sl1 = 0.5 * Math.sqrt(uc.d_hkl(ah, bh, ch))
        if sl1 <= 5.0 / lambda // this spherical restriction on reflections allows to keep almost exact hexagonal symmetry
        sl2 = 0.5 * Math.sqrt(uc.d_hkl(ah2, bh2, ch2))
        if sl2 <= 5.0 / lambda
      } yield (ah, bh, ch)

      type StructureFactor = Try[((Int,Int,Int),Complex,Complex,Complex)]

      @strictfp def calcStructFactor: ((Int,Int,Int)) => StructureFactor = {
        case (ah, bh, ch) => for {
          fh1 <- structure_factor(uc, ah, bh, ch, atoms, lambda)
          fh2 <- structure_factor(uc, params.h - ah, params.k - bh, params.l - ch, atoms, lambda)
//          if fh1.normSquare >= 1.0E-6 // this excludes zero reflections
//          if fh2.normSquare >= 1.0E-6
          p = fh1 * fh2
//          if p.normSquare >= 1.0E1 // was 5 this excludes small umweg reflections
        } yield ((ah, bh, ch), p, fh1, fh2)
      }

      @strictfp def noSmallFactors: StructureFactor => Boolean = {
        case Success((_, p, fh1, fh2)) => p.normSquare >= 1.0E1 && fh1.normSquare >= 1.0E-6 && fh2.normSquare >= 1.0E-6
        case _ => true
      }

      val factors = if (params.parallelCalc) (indices.par map calcStructFactor filter noSmallFactors).seq
      else indices map calcStructFactor filter noSmallFactors

      Logger.incrementProgress()

      Logger.log(s"ncount = ${factors.length}")

      // Calculating basis for incident wave vector
      val n = (params.h * aRec + params.k * bRec + params.l * cRec).norm

      var a0: P3 = P3(0, 0, 0)
      var b0: P3 = P3(0, 0, 0)

      if (Math.abs(a.norm.dot(n)) > Math.abs(b.norm.dot(n)) && // plane more perpendicular to lattice a basis vector
        Math.abs(a.norm.dot(n)) > Math.abs(c.norm.dot(n))) {
        a0 = b.norm
        b0 = c.norm
      }
      else if (Math.abs(b.norm.dot(n)) > Math.abs(a.norm.dot(n)) && // plane more perpendicular to lattice b basis vector
        Math.abs(b.norm.dot(n)) > Math.abs(c.norm.dot(n))) {
        a0 = c.norm
        b0 = a.norm
      }
      else // plane more perpendicular to lattice c basis vector
      {
        a0 = a.norm
        b0 = b.norm
      }

      val nn = a0.cross(b0).norm

      if (1e-10 < Math.sqrt(n.cross(nn).normSquare)) {
        a0 = n.cross(nn)
      }

      b0 = n.cross(a0)

      @strictfp def indexToPsi(i: Int) = {
        if (params.psiSteps == 0) params.psiStart
        else params.psiStart + (i - 1) * (params.psiEnd - params.psiStart) / params.psiSteps
      }

      val Psi = (1 to (params.psiSteps + 1) map { i =>
        if (params.psiSteps == 0) params.psiStart
        else params.psiStart + (i - 1) * (params.psiEnd - params.psiStart) / params.psiSteps
      }).par

      @strictfp def reducePlanes(psi: Double): (Double, Double, Double, Double, Double, Double, Double, Double, Double, Complex, Complex, Complex, Complex, Complex, Complex, Complex, (Int, Int, Int)) = {
        params.sweep match {
          case ParametersSweep.PSI =>
            Logger.log(s"Psi = $psi")
          case ParametersSweep.ENERGY =>
            ()
          case ParametersSweep.BOTH =>
            ()
        }

        // the wavevector of the incident wave vs Psi for 00\ell reflection
        // and incident and scattered polarization vectors
        val kx = wavevec * Math.cos(thetab * rad) * Math.cos(psi * rad)
        val ky = wavevec * Math.cos(thetab * rad) * Math.sin(psi * rad)
        val kz = -wavevec * Math.sin(thetab * rad)
        val k = kx * a0 + ky * b0 + kz * n
        // incident polarization vectors
        val esx = -Math.sin(psi * rad) // es - sigma polarization of the initial wave
        val esy = Math.cos(psi * rad)
        val esz = 0.0
        val es = esx * a0 + esy * b0 + esz * n
        val epx = Math.sin(thetab * rad) * Math.cos(psi * rad) //ep - pi polarization of the initial wave
        val epy = Math.sin(thetab * rad) * Math.sin(psi * rad)
        val epz = Math.cos(thetab * rad)
        val ep = epx * a0 + epy * b0 + epz * n
        // incident polarization vector
        val ep1x = -Math.sin(thetab * rad) * Math.cos(psi * rad) //ep1 - pi polarization of the scattered wave
        val ep1y = -Math.sin(thetab * rad) * Math.sin(psi * rad)
        val ep1z = Math.cos(thetab * rad)
        val ep1 = ep1x * a0 + ep1y * b0 + ep1z * n
        // END of wavevector and polarizations
        val f = factors.foldLeft(Success((Complex(0, 0), Complex(0, 0), Complex(0, 0), Complex(0, 0), -1.0, (Int.MinValue, Int.MinValue, Int.MinValue))): Try[(Complex, Complex, Complex, Complex, Double, (Int, Int, Int))]) {
          case (Success((ss, pp, ps, sp, maxFactor, planes)), Success(((ah, bh, ch), fh, _, _))) =>
            //val kn2 = (kx + aRec * ah) ** 2 + (ky + bRec * bh) ** 2 + (kz + cRec * ch) ** 2 //this is \mathbf{k}_n^2
            val p = aRec * ah + bRec * bh + cRec * ch
            val kn2 = (k + p).normSquare //this is \mathbf{k}_n^2
            //val kns = (kx + aRec * ah) * esx + (ky + bRec * bh) * esy + (kz + cRec * ch) * esz //this is \mathbf{k}_n\cdot\mathbf{\sigma}
            val kns = (k + p) * es //this is \mathbf{k}_n\cdot\mathbf{\sigma}
            //val knp = (kx + aRec * ah) * epx + (ky + bRec * bh) * epy + (kz + cRec * ch) * epz //this is \mathbf{k}_n\cdot\mathbf{\pi}
            val knp = (k + p) * ep //this is \mathbf{k}_n\cdot\mathbf{\pi}
            //val knp1 = (kx + aRec * ah) * ep1x + (ky + bRec * bh) * ep1y + (kz + cRec * ch) * ep1z //this is \mathbf{k}_n\cdot\mathbf{\pi'}
            val knp1 = (k + p) * ep1 //this is \mathbf{k}_n\cdot\mathbf{\pi'}
            //     Dividing on 0 here create a peak
            val ffactor = fh / (kn2 * (1.0 - chi0) - wavevec ** 2)
            val ffactorAbs = ffactor.normSquare
            Success((ss + ffactor * (kn2 - kns ** 2), pp + ffactor * (kn2 * Math.cos(pi * thetab / 90.0) - knp1 * knp),
              ps - ffactor * knp1 * kns, sp - ffactor * kns * knp, if (maxFactor < ffactorAbs) ffactorAbs else maxFactor,
              if (maxFactor < ffactorAbs) (ah, bh, ch) else planes
            ))
          case (err: Failure[_], _) => err
          case (_, Failure(err)) => Failure(err)
        }

        if (f.isFailure) {
          Logger.log(f.failed.get.getMessage)
          return (Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
            Double.NaN, Complex(Double.NaN,Double.NaN), Complex(Double.NaN,Double.NaN), Complex(Double.NaN,Double.NaN),
            Complex(Double.NaN,Double.NaN), Complex(Double.NaN,Double.NaN), Complex(Double.NaN,Double.NaN),
            Complex(Double.NaN,Double.NaN), (Int.MinValue, Int.MinValue, Int.MinValue))
        }

        val (fMss, fMpp, fMps, fMsp, _, maxPlanes) = f.get
        val cQQ = fQQ * Math.sin(3 * pi * psi / 180.0)
        val fMultss = fMss * 4.0 * pi * r0 / (wavevec ** 2 * vol)
        val fMultpp = fMpp * 4.0 * pi * r0 / (wavevec ** 2 * vol)
        val fMultps = fMps * 4.0 * pi * r0 / (wavevec ** 2 * vol)
        val fMultsp = fMsp * 4.0 * pi * r0 / (wavevec ** 2 * vol)
        val fMod2ss = (fMultss + fMag).re ** 2 + (fMultss + fMag).im ** 2
        val fMod2pp = (fMultpp + fMag).re ** 2 + (fMultpp + fMag).im ** 2
        val fMod2ps = (fMultps - fMag + cQQ + fDQ).re ** 2 + (fMultps - fMag + cQQ + fDQ).im ** 2
        val fMod2sp = (fMultsp + fMag + cQQ - fDQ).re ** 2 + (fMultsp + fMag + cQQ - fDQ).im ** 2
        val fMod2s = fMod2ps + fMod2ss
        val fMod2p = fMod2pp + fMod2sp
        val rr2 = ((fMultss + edi * fMultsp - edi * fMag).normSquare + (edi * fMultpp + fMultps + fMag).normSquare) / 2.0
        val rl2 = ((fMultss - edi * fMultsp + edi * fMag).normSquare + (-edi * fMultpp + fMultps + fMag).normSquare) / 2.0

        Logger.incrementProgress()

        (psi, rr2, rl2, fMod2ss, fMod2pp, fMod2ps, fMod2sp, fMod2s, fMod2p, fMultss, fMultpp, fMultps, fMultsp, cQQ, fDQ, fMag, maxPlanes)
      }

      val results = if (params.parallelCalc) (Psi.par map reducePlanes).seq else { Psi map reducePlanes }

      Logger.log("Writing data")

      for ( (psi, rr2, rl2, fMod2ss, fMod2pp, fMod2ps, fMod2sp, fMod2s, fMod2p, fMultss, fMultpp, fMultps, fMultsp, cQQ, fDQ, fMag, maxPlanes) <- results) {
        params.sweep match {
          case ParametersSweep.PSI =>
            azimLin.print(s"$psi ; ${maxPlanes._1}; ${maxPlanes._2}; ${maxPlanes._3} ; $fMod2ss ; $fMod2pp ; $fMod2ps ; $fMod2sp ; $fMod2s ; $fMod2p ; ")
            azimLin.print(s"${fMultss.re} ; ${fMultss.im} ; ")
            azimLin.print(s"${fMultpp.re} ; ${fMultpp.im} ; ${fMultps.re} ; ${fMultps.im} ; ")
            azimLin.print(s"${fMultsp.re} ; ${fMultsp.im} ; ")
            azimLin.print(s"${cQQ.re} ; ${cQQ.im} ; ${fDQ.re} ; ${fDQ.im} ; ")
            azimLin.println(s"${fMag.re} ; ${fMag.im}")
          case ParametersSweep.ENERGY =>
            azimLin.print(s"$energy ; ${maxPlanes._1}; ${maxPlanes._2}; ${maxPlanes._3} ; $fMod2ss ; $fMod2pp ; $fMod2ps ; $fMod2sp ; $fMod2s ; $fMod2p ; ")
            azimLin.print(s"${fMultss.re} ; ${fMultss.im} ; ")
            azimLin.print(s"${fMultpp.re} ; ${fMultpp.im} ; ${fMultps.re} ; ${fMultps.im} ; ")
            azimLin.print(s"${fMultsp.re} ; ${fMultsp.im} ; ")
            azimLin.print(s"${cQQ.re} ; ${cQQ.im} ; ${fDQ.re} ; ${fDQ.im} ; ")
            azimLin.println(s"${fMag.re} ; ${fMag.im}")
          case ParametersSweep.BOTH =>
            ()
        }

        params.sweep match {
          case ParametersSweep.PSI =>
            azimCirc.println(s"$psi ; ${maxPlanes._1}; ${maxPlanes._2}; ${maxPlanes._3} ; $rr2 ; $rl2 ; ${(rr2 - rl2) / (rr2 + rl2)} ; ${(rr2 + rl2) / 2.0}")
          case ParametersSweep.ENERGY =>
            azimCirc.println(s"$energy ; ${maxPlanes._1}; ${maxPlanes._2}; ${maxPlanes._3} ; $rr2 ; $rl2 ; ${(rr2 - rl2) / (rr2 + rl2)} ; ${(rr2 + rl2) / 2.0}")
          case ParametersSweep.BOTH =>
            azimCirc.print(s"; $rr2")
            azimLin.print(s"; $rl2")
            peaks.print(s"; (${maxPlanes._1}, ${maxPlanes._2}, ${maxPlanes._3})")
        }
      }

      params.sweep match {
        case ParametersSweep.PSI =>
          azimLin.close()
          azimCirc.close()
        case ParametersSweep.BOTH =>
          azimCirc.println()
          azimLin.println()
          peaks.println()
        case _ =>
          ()
      }
    }

    params.sweep match {
      case ParametersSweep.ENERGY =>
        azimLin.close()
        azimCirc.close()
      case ParametersSweep.BOTH =>
        azimCirc.close()
        azimLin.close()
        peaks.close()
      case _ =>
        ()
    }
  }
}

object Calc {
  def apply(unitCell: UnitCell, atoms: Array[AtomDescr], params: CalcParams): Unit = {
    val c = new Calc
    c(unitCell, atoms, params)
  }
}
