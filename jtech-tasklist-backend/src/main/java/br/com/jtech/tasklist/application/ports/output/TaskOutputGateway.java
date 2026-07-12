package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;
import br.com.jtech.tasklist.application.core.domains.Task;

public interface TaskOutputGateway {
    TaskResponse create(Task task);
    TaskResponse update(Task task);
    TaskResponse delete(Task Task);
}
