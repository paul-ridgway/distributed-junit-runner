package io.ridgway.paul.tests.executor;

import io.ridgway.paul.tests.api.TestService;
import org.apache.thrift.TException;

@FunctionalInterface
public interface ClientFunction<R> {

    R execute(final TestService.Client client) throws TException;

}
