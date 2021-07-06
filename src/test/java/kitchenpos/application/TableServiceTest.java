package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class TableServiceTest {
	@Autowired
	private TableService tableService;

	@Test
	@DisplayName("주문테이블 생성 성공. 테이블 그룹 없음. guest 1명")
	public void 주문테이블_생성_성공_테이블그룹없음_혼밥러() {
		OrderTable orderTable = new OrderTable();
		orderTable.setNumberOfGuests(1);
		orderTable.setEmpty(false);

		OrderTable savedOrderTable = tableService.create(orderTable);

		assertThat(savedOrderTable.getTableGroupId()).isNull();
		assertThat(savedOrderTable.getNumberOfGuests()).isEqualTo(1);
		assertThat(savedOrderTable.isEmpty()).isFalse();
	}

	@Test
	public void 주문테이블_리스트조회() {
		OrderTable orderTable = new OrderTable();
		orderTable.setNumberOfGuests(1);
		orderTable.setEmpty(false);

		tableService.create(orderTable);

		List<OrderTable> orderTables = tableService.list();

		assertThat(orderTables)
				.extracting("numberOfGuests", "empty")
				.contains(tuple(1, false));
	}

	@Test
	@DisplayName("주문테이블 주문취소처리 실패. 주문 테이블 정보 없음")
	public void 주문테이블_주문없음_처리_실패_주문테이블정보없음() {
		assertThatThrownBy(() -> tableService.changeEmpty(9999L, new OrderTable()))
				.isInstanceOf(IllegalArgumentException.class);
	}

	//	@Test TODO with Order Test
	public void 주문테이블_주문없음_처리_실패_주문테이블그룹정보있음() {
		OrderTable orderTable = new OrderTable();
		orderTable.setNumberOfGuests(1);
		orderTable.setEmpty(false);

		OrderTable savedOrderTable = tableService.create(orderTable);

		savedOrderTable.setTableGroupId(1L);
		assertThatThrownBy(() -> tableService.changeEmpty(savedOrderTable.getId(), savedOrderTable))
				.isInstanceOf(IllegalArgumentException.class);
	}

	//	@Test TODO 테스트가 집중되도록 리팩터링
	public void 주문테이블_주문없음_처리_실패_주문상태가_COOKING_이면_변경안됨() {
		OrderTable orderTable = new OrderTable();
		orderTable.setNumberOfGuests(1);
		orderTable.setEmpty(false);

		OrderTable savedOrderTable = tableService.create(orderTable);

		assertThatThrownBy(() -> tableService.changeEmpty(savedOrderTable.getId(), savedOrderTable))
				.isInstanceOf(IllegalArgumentException.class);
	}

	//	@Test TODO 테스트가 집중되도록 리팩터링
	public void 주문테이블_주문없음_처리_실패_주문상태가_MEAL_이면_변경안됨() {
		OrderTable orderTable = new OrderTable();
		orderTable.setNumberOfGuests(1);
		orderTable.setEmpty(false);

		OrderTable savedOrderTable = tableService.create(orderTable);

		assertThatThrownBy(() -> tableService.changeEmpty(savedOrderTable.getId(), savedOrderTable))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void 주문테이블_방문한_손님수_변경_성공() {
		OrderTable orderTable = new OrderTable();
		orderTable.setNumberOfGuests(1);
		orderTable.setTableGroupId(1L);
		orderTable.setEmpty(false);

		OrderTable savedOrderTable = tableService.create(orderTable);

		savedOrderTable.setNumberOfGuests(3); // Change Number Of Guest
		tableService.changeNumberOfGuests(savedOrderTable.getId(), savedOrderTable);

		assertThat(savedOrderTable.getTableGroupId()).isNull();
		assertThat(savedOrderTable.getNumberOfGuests()).isEqualTo(3);
		assertThat(savedOrderTable.isEmpty()).isFalse();
	}

	@Test
	@DisplayName("주문테이블 방문 손님수 변경 실패. 손님의 수는 0이상 자연수여야한다.")
	public void 주문테이블_방문한_손님수_변경_실패_손님수는_0_이상자연수() {
		OrderTable orderTable = new OrderTable();
		orderTable.setNumberOfGuests(0);
		orderTable.setTableGroupId(1L);
		orderTable.setEmpty(false);

		OrderTable savedOrderTable = tableService.create(orderTable);

		savedOrderTable.setNumberOfGuests(-1); // Change Number Of Guest
		assertThatThrownBy(() -> tableService.changeNumberOfGuests(savedOrderTable.getId(), savedOrderTable))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("주문테이블 방문 손님 수 변경 실패. 빈테이블은 방문 손님 수를 변경할 수 없다.")
	public void 주문테이블_방문한_손님수_변경_실패_빈테이블은_변경이아니고_생성하자() {
		OrderTable orderTable = new OrderTable();
		orderTable.setNumberOfGuests(0);
		orderTable.setTableGroupId(1L);
		orderTable.setEmpty(true);

		OrderTable savedOrderTable = tableService.create(orderTable);

		savedOrderTable.setNumberOfGuests(3); // Change Number Of Guest
		assertThatThrownBy(() -> tableService.changeNumberOfGuests(savedOrderTable.getId(), savedOrderTable))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void 주문테이블_주문없음_처리_성공() {
		OrderTable orderTable = new OrderTable();
		orderTable.setNumberOfGuests(1);
		orderTable.setTableGroupId(1L);
		orderTable.setEmpty(false);

		OrderTable savedOrderTable = tableService.create(orderTable);

		tableService.changeEmpty(savedOrderTable.getId(), savedOrderTable);
	}
}