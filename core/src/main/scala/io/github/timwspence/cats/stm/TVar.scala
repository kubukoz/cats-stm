package io.github.timwspence.cats.stm

import java.util.concurrent.atomic.AtomicReference

import io.github.timwspence.cats.stm.STM.internal._

/**
  * Transactional variable - a mutable memory location
  * that can be read or written to via `STM` actions.
  *
  * Analagous to `cats.effect.concurrent.Ref`.
  */
final class TVar[A] private[stm] (
  private[stm] val id: Long,
  @volatile private[stm] var value: A,
  private[stm] val pending: AtomicReference[Map[TxId, RetryFiber]]
) {

  /**
    * Get the current value as an
    * `STM` action.
    */
  def get: STM[A] = Get(this)

  /**
    * Set the current value as an
    * `STM` action.
    */
  def set(a: A): STM[Unit] = modify(_ => a)

  /**
    * Modify the current value as an
    * `STM` action.
    */
  def modify(f: A => A): STM[Unit] = Modify(this, f)

}

object TVar {

  def of[A](value: A): STM[TVar[A]] = Alloc(value)
    // Pure(new TVar(IdGen.incrementAndGet(), value, new AtomicReference(Map())))

  val tvarDebug: AtomicReference[Set[Long]] = new AtomicReference(Set.empty)

}
