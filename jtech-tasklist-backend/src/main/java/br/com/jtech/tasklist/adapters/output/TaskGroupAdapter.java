package br.com.jtech.tasklist.adapters.output;

import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.jtech.tasklist.application.core.domains.TaskGroup;
import br.com.jtech.tasklist.application.ports.output.TaskGroupOutputGateway;

@Repository
public class TaskGroupAdapter implements TaskGroupOutputGateway {

    @Override
    public TaskGroup create(TaskGroup taskGroup) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public TaskGroup findById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public List<TaskGroup> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }
}
