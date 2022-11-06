package hello.core.scope;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Provider;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 싱글톤 빈과 프로토타입 빈을 함께 사용시 문제점
 */
public class SingletonWithPrototypeProvider {

    @Test
    void singletonClientUsePrototype() {
        // 1. 싱글톤 빈은 스프링 컨테이너 생성 시점에 함께 생성되고, 의존관계 주입도 발생하므로 주입 시점에 스프링 컨테이너에 프로토타입 빈을 요청한다.
        // 2. 스프링 컨테이너는 프로토타입 빈을 생성해서 싱글톤 빈에 반환하고, 싱글톤 빈은 프로토타입 빈을 내부 필드에 참조값을 보관한다.
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);

        // 3. 클라이언트 A가 싱글톤 빈을 스프링 컨테이너에 요청해서 받을 때, 싱글톤 빈은 프로토타입 빈을 실행한다.
        ClientBean client1 = ac.getBean(ClientBean.class);
        int count1 = client1.logic();
        assertThat(count1).isEqualTo(1);

        // 4. 클라이언트 B가 싱글톤 빈을 스프링 컨테이너에 요청해서 받는다. (A와 같은 참조값. 당연하지 싱글톤이니까)
        // 5. 싱글톤 빈이 프로토타입 빈을 실행할 때, 이미 A에서 사용했던 프로토타입 빈을 또 사용해버린다..!
        ClientBean client2 = ac.getBean(ClientBean.class);
        int count2 = client2.logic();
        assertThat(count2).isEqualTo(1);
    }

    @Scope
    @RequiredArgsConstructor
    static class ClientBean {
        private final Provider<PrototypeBean> prototypeBeanProvider;

        public int logic() {
            PrototypeBean prototypeBean = prototypeBeanProvider.get();
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }
    }
}
