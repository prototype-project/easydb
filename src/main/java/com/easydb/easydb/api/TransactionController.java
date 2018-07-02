package com.easydb.easydb.api;

import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.TransactionManager;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/transactions")
class TransactionController {

	private final TransactionManager manager;
	private final UUIDProvider uuidProvider;

	TransactionController(TransactionManager manager, UUIDProvider uuidProvider) {
		this.manager = manager;
		this.uuidProvider = uuidProvider;
	}

	@PostMapping("/{spaceName}")
	@ResponseStatus(value = HttpStatus.CREATED)
	String beginTransaction(@PathVariable("spaceName") String spaceName) {
		return manager.beginTransaction(spaceName);
	}

	@PostMapping("/add-operation/{transactionId}")
	@ResponseStatus(value = HttpStatus.CREATED)
	void addOperation(@PathVariable String transactionId,
	                  @RequestBody @Valid OperationDto dto) {
		manager.addOperation(transactionId, dto.toDomain(uuidProvider));
	}
}
