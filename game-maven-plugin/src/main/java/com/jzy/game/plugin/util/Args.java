package com.jzy.game.plugin.util;

/**
 * 参数对象
 *
 * @author JiangZhiYong
 * @date 2017-03-03
 * QQ:359135103
 */
public interface Args {

    static <A> Args.One<A> of(A a) {
        return () -> a;
    }

    static <A, B> Args.Two<A, B> of(A a, B b) {
        return new Two<A, B>() {
            @Override
            public A a() {
                return a;
            }

            @Override
            public B b() {
                return b;
            }
        };
    }

    static <A, B, C> Args.Three<A, B, C> of(A a, B b, C c) {
        return new Three<A, B, C>() {
            @Override
            public A a() {
                return a;
            }

            @Override
            public B b() {
                return b;
            }

            @Override
            public C c() {
                return c;
            }
        };
    }

    static <A, B, C, D> Args.Four<A, B, C, D> of(A a, B b, C c, D d) {
        return new Four<A, B, C, D>() {
            @Override
            public A a() {
                return a;
            }

            @Override
            public B b() {
                return b;
            }

            @Override
            public C c() {
                return c;
            }

            @Override
            public D d() {
                return d;
            }
        };
    }

    static <A, B, C, D, E> Args.Five<A, B, C, D, E> of(A a, B b, C c, D d, E e) {
        return new Five<A, B, C, D, E>() {
            @Override
            public A a() {
                return a;
            }

            @Override
            public B b() {
                return b;
            }

            @Override
            public C c() {
                return c;
            }

            @Override
            public D d() {
                return d;
            }

            @Override
            public E e() {
                return e;
            }
        };
    }

    interface One<A> extends Args {
        A a();
    }

    interface Two<A, B> extends Args {
        A a();

        B b();
    }

    interface Three<A, B, C> extends Args {
        A a();

        B b();

        C c();
    }

    interface Four<A, B, C, D> extends Args {
        A a();

        B b();

        C c();

        D d();
    }

    interface Five<A, B, C, D, E> extends Args {
        A a();

        B b();

        C c();

        D d();

        E e();
    }
}
