
package com.lis.ipc;

import com.lis.ipc.model.Request;
import com.lis.ipc.model.Response;
interface IIPCService {
   Response send(in Request request);
}
