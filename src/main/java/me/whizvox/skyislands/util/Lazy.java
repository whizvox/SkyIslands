package me.whizvox.skyislands.util;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@NotNullByDefault
public class Lazy<T> {

  private @Nullable T obj;
  private final Supplier<T> supplier;

  private Lazy(Supplier<T> supplier) {
    obj = null;
    this.supplier = supplier;
  }

  public T get() {
    if (obj != null) {
      return obj;
    }
    return obj = supplier.get();
  }

  public static <T> Lazy<T> of(Supplier<T> supplier) {
    return new Lazy<>(supplier);
  }

}
