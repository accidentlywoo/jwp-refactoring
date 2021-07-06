package kitchenpos.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class TableGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDateTime createdDate;

	@OneToMany(mappedBy = "tableGroup")
	private List<OrderTable> orderTables;

	protected TableGroup() {
	}

	public TableGroup(Long id, LocalDateTime createdDate) {
		this.id = id;
		this.createdDate = createdDate;
	}
}
